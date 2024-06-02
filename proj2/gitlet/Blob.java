package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;


/**
 * Blob是文件在add时的快照
 * 储存了文件的一个暂时的版本
 *
 * @author UchuVoid
 */
public class Blob implements Serializable {
    public static final File blobs = join(Repository.GITLET_DIR, "Blobs");
    private final String fileName;
    private final String fileContent;
    private final String id;

    /**
     * 创建一个储存文件各种版本的文件夹，
     * 在这个文件夹中储存这以文件SHA-1值
     * 为文件名的各种文件
     *
     * @param fileName 被add的文件的名字
     * @param addFile  被add的文件
     */
    public Blob(String fileName, File addFile) {
        if (!blobs.exists()) {
            blobs.mkdirs();
        }
        this.fileName = fileName;
        //将该文件内含的字符串储存
        this.fileContent = readContentsAsString(addFile);
        id = sha1(this.fileName + this.fileContent);
    }

    /**
     * 将blob保存到Blobs文件夹中
     */
    public void saveBlob() {
        File blobFile = join(blobs, this.id);
        try {
            blobFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(blobFile, this);
    }

    /**
     * 根据SHA_1值
     * 判断两个Blob是否相同
     */
    public boolean compareTo(String otherId) {
        if (otherId.equals(this.id)) {
            return true;
        }
        return false;
    }

    /**
     * 输入另一个blob
     * 判断两个Blob是否相同
     */
    public boolean compareTo(Blob otherBlob) {
        if (otherBlob == null) {
            return false;
        }
        String otherId = otherBlob.getId();
        return this.compareTo(otherId);
    }


    /**
     * 根据BlobId返回blobs文件夹中指定blob
     */
    public static Blob getBlob(String blobId) {
        File blobFile = join(blobs, blobId);
        if (!blobFile.exists()) {
            return null;
        }
        return readObject(blobFile, Blob.class);
    }

    //将该文件覆盖到工作区
    public void coverWorkFile() throws IOException {
        File workBlob = join(Repository.CWD, this.fileName);
        //如果存在同名文件覆盖掉
        if (workBlob.exists()) {
            Utils.restrictedDelete(this.fileName);
        }
        workBlob.createNewFile();
        writeContents(workBlob, this.fileContent);
    }

    //判断改blob是否修改
    @Override
    public boolean equals(Object obj) {
        Blob other = (Blob) obj;
        return this.fileContent.equals(other.getFileContent());
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getId() {
        return id;
    }


}
