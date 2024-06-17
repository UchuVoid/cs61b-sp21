package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.readObject;

/**
 * Represents a gitlet commit object.
 * <p>
 * A commit consists of a log message, timestamp,
 * a mapping of file names to blob references, a parent reference,
 * and (for merges) a second parent reference.
 *
 * @author UchuVoid
 */
public class Commit implements Serializable {
    public final static File COMMIT_FOLDER = join(Repository.GITLET_DIR, "commit");

    /**
     * The message of this Commit.
     */
    private String message;
    /* The timestamp of current time*/
    private Date timestamp;
    /**
     * Mapping of `filenames` to corresponding `Blob` objects.
     * Example of mapping: {"hello.txt": "someSHA-1Hash"}
     */
    private TreeMap<String, String> nameToBlob; //
    /**
     * Parent of the current Commit: a sha-1 hash.
     */
    private final List<String> parents;
    private final String id;

    /**
     * Constructor.
     *
     * @param msg       Commit message.
     * @param parents   Parent of the Commit instance.
     * @param timestamp Timestamp of the Commit instance.
     */
    public Commit(String msg, List<String> parents, TreeMap<String, String> nameToBlob, Date... timestamp) {
        this.message = msg;
        this.timestamp = timestamp[0];
        this.parents = parents;
        this.nameToBlob = nameToBlob;
        id = Utils.sha1(this.message + this.timestamp.toString() + this.parents
                + this.nameToBlob.toString());
    }

    /**
     * save the commit to the COMMIT_FOLDER
     */
    public void saveCommit() {
        if (!COMMIT_FOLDER.exists()) {
            COMMIT_FOLDER.mkdir();
        }
        /** 在commit文件夹中创建一个以SHA_1值为名的文件
         * 内含commit */
        File commitFile = new File(COMMIT_FOLDER, this.id);

        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Serialize the Commit and save it to COMMIT_FOLDER
        writeObject(commitFile, this);
    }


    /**
     * Adds a parent to the current parent list.
     * Used for merging commits.
     */
    public void addParent(Commit parent) {
        this.parents.add(parent.getId());
        this.saveCommit();// update commit file
    }

    /**
     * 返回该commit中指定文件名的SHA_1值
     */
    public String getBlobId(String fileName) {
        return nameToBlob.get(fileName);
    }

    /**
     * 根据文件路径
     * 返回指定分支所指的commit
     * 用于从分支中提取commit
     */
    public static Commit getCommit(File p) {

        /** 返回p指向的commit的hash值 */
        String hashInPoint = readContentsAsString(p);

        File pointPath = join(COMMIT_FOLDER, hashInPoint);

        if (!pointPath.exists()) {
            return null;
        }

        // Return the Commit obj if it exists
        return readObject(pointPath, Commit.class);
    }

    /**
     * 根据commit的id
     * 返回COMMIT_DIR中指定的commit
     * 用于从分支中提取commit
     */
    public static Commit getCommit(String id) {
        int n = id.length();
        String foundId = id;
        //简短型id的长度
        int cutId = 6;
        //搜索简短型的id
        if (n == cutId) {
            // Search id in file (might be abbreviated id)
            for (String name : Objects.requireNonNull(plainFilenamesIn(COMMIT_FOLDER))) {
                if (name.substring(0, n).equals(id)) {
                    foundId = name;
                }
            }
        }
        File pointPath = join(COMMIT_FOLDER, foundId);
        if (!pointPath.exists()) {
            return null;
        }
        return readObject(pointPath, Commit.class);
    }

    /**
     * 根据parent的id
     * 返回指定的parent Commit
     */
    public Commit getParent(int index) {
        if (parents == null || parents.size() == 0) {
            return null;
        }
        String parentId = parents.get(index);
        File parentFile = join(COMMIT_FOLDER, parentId);
        if (!parentFile.exists()) {
            return null;
        }
        return readObject(parentFile, Commit.class);
    }


    /**
     * Update current Commit according to staged addition and removal.
     */
    public void mergeBlob(StageArea stageArea) {
        TreeMap<String, String> addBlob = stageArea.getNameToBlob();
        List<String> rmBlob = stageArea.getRmFile();

        for (String addFile : addBlob.keySet()) {
            nameToBlob.put(addFile, addBlob.get(addFile));
        }

        for (String rmFile : rmBlob) {
            nameToBlob.remove(rmFile);
        }
    }

    /**
     * 根据指定文件名返回Blob
     */
    public Blob getBlob(String fileName) {
        String blobId = getBlobId(fileName);
        if (blobId == null) {
            return null;
        }
        return Blob.getBlob(blobId);
    }

    /**
     * 打印commit的log信息
     */
    public void printCommitInfo() {
        SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        message("===");
        message("commit %s", id);
        if (parents != null && parents.size() == 2) {
            Commit parent1 = getParent(0);
            Commit parent2 = getParent(1);
            System.out.printf(
                    "Merge: %s %s\n",
                    parent1.getId().substring(0, 7),
                    parent2.getId().substring(0, 7));
        }
        message("Date: " + format.format(timestamp));
        message(this.message + "\n");
    }

    /**
     * 将该commit所有Blob的版本覆盖到工作区
     */
    public void coverFile() throws IOException {
        Commit curCommit = getCommit(Repository.HEAD);
        Map<String, String> names = new HashMap<>();
        names.putAll(curCommit.getNameToBlob());
        names.putAll(nameToBlob);
        /* There are 2 kinds of situations: */
        for (String blobName : names.keySet()) {
            //在branchCommit中存在同名blob替换掉
            if (nameToBlob.containsKey(blobName)) {
                String blobId = nameToBlob.get(blobName);
                Blob blob = Blob.getBlob(blobId);
                if (blob != null) {
                    blob.coverWorkFile();
                }
            } else { //不存在则在工作区中删除
                File rmFile = join(Repository.CWD, blobName);
                restrictedDelete(rmFile);
            }
        }


    }

    /**
     * 找到两个commit共同的祖先
     */
    public static Commit findSplitPoint(Commit commit1, Commit commit2) {
        Set<String> ancestors = new HashSet<>();

        // 从第一个提交开始向上遍历，记录所有祖先节点
        Commit current = commit1;
        while (current != null) {
            ancestors.add(current.getId());
            current = current.getParent(0);
        }

        // 从第二个提交开始向上遍历，查找第一个出现在祖先集合中的节点
        current = commit2;
        while (current != null) {
            String branchId = current.getId();
            if (ancestors.contains(branchId)) {
                return current; // 找到最近公共祖先
            }
            current = current.getParent(0);
        }

        return null; // 如果没有找到公共祖先，则返回null
    }

    public String getParentId(int index) {
        return parents.get(index);
    }

    public boolean containsBlob(String fileName) {
        return nameToBlob.containsKey(fileName);
    }

    public TreeMap<String, String> getNameToBlob() {
        return nameToBlob;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp.toString();
    }

    public String getMessage() {
        return message;
    }

    //判断是否改commit版本中是否含有该文件
    public boolean containsFile(String fileName) {
        return nameToBlob.containsKey(fileName);
    }
}
