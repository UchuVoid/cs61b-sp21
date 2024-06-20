package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.plainFilenamesIn;


/**
 * Represents a gitlet repository.
 *
 * @author UchuVoid
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /* Directory */
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The directory in .gitlet/ which stores the heads of branches (incl. Master and others).
     */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branchHeads");

    /* File */
    /**
     * HEAD stores what's the current commit the head is pointing to
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /**
     * storing which commit the master is pointing to
     */
    public static final File MASTER = join(BRANCH_DIR, "master");

    /**
     * add后文件暂存区文件的路径
     */
    public static final File STAGE_FILE = join(GITLET_DIR, "stageArea");

    public static final File blobs = join(Repository.GITLET_DIR, "blobs");

    /**
     * 储存当前所在分支
     * 分支可以是HEAD也可以是其他如master的分支
     */
    public static final File curBranch = join(GITLET_DIR, "curBranch");

    /**
     * 初始化.gitlet文件夹
     */
    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        /**初始化各种文件及文件夹*/
        initDirs();
        /** Creat the first Commit*/
        Commit firstCommit = new Commit("initial commit", null, new TreeMap<>(), new Date(0));
        firstCommit.saveCommit();

        /**
         * 初始化缓存区
         */
        StageArea stageArea = new StageArea(STAGE_FILE);
        stageArea.saveStage();
        /** 同步将各种分支指向的commit */
        updatePointerTo(HEAD, firstCommit);
        updatePointerTo(MASTER, firstCommit);
        /** 设置默认分支为master */
        updateBranch(curBranch, "master");

    }

    /**
     * Updates a pointer P to point to a specific Commit
     * Usage: updatePointerTo(HEAD, commit) HEAD -> commit
     * updatePointerTo(Master, commit) Master -> commit
     */
    private static void updatePointerTo(File p, Commit commit) {
        /*update by internally overwriting the hash of the commit*/
        writeContents(p, commit.getId());
    }

    /**
     * 更新当前所在分支
     */

    /**
     * 更新当前所在分支
     *
     * @param p      储存当前文件分支的文件
     * @param branch 新的分支
     * @throws IOException
     */
    private static void updateBranch(File p, String branch) throws IOException {
        writeContents(p, branch);
    }

    /**
     * 初始化.gitlet文件夹中的文件及文件夹
     */
    private static void initDirs() throws IOException {
        //directions
        GITLET_DIR.mkdirs();
        BRANCH_DIR.mkdirs();
        blobs.mkdirs();
        //files
        HEAD.createNewFile();
        MASTER.createNewFile();
        STAGE_FILE.createNewFile();
        curBranch.createNewFile();
    }

    /**
     * 添加文件添加到Blob文件夹中
     * 并将文件添加到暂存区
     */
    public static void add(String fileName) {

        File path = join(CWD, fileName);
        //判断add文件是否存在
        if (!path.exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
        }

        Blob newBlob = new Blob(fileName, path);

        /** 判断add文件相较前一次commit是否被修改 */
        Commit headCommit = Commit.getCommit(HEAD);

        /** 前一次提交时的该文件 */
        Blob headBlob = headCommit.getBlob(fileName);

        /** 如果相同不存入缓存区 */
        if (newBlob.compareTo(headBlob)) {
            return;
        }

        newBlob.saveBlob();

        /** 初始化暂存区 */
        StageArea addArea = readObject(STAGE_FILE, StageArea.class);

        /** 将文件对应关系储存到暂存区 */
        addArea.addBlob(newBlob);
        addArea.saveStage();

    }

    /**
     * 将缓存区的修改提交
     * 并清空缓存区
     */
    public static Commit commit(String msg) {
        //判断是否输入提示信息
        if (msg == null || msg.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return null;
        }
        //前一次提交的commit
        StageArea stageArea = readObject(STAGE_FILE, StageArea.class);
        if (stageArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return null;
        }

        // Clone the parent Commit and update meta data
        Commit parentCommit = Commit.getCommit(HEAD);
        List<String> parents = new ArrayList<>();
        parents.add(parentCommit.getId());
        Commit curCommit = new Commit(msg, parents, parentCommit.getNameToBlob(), new Date()); // receives a copy of parent's map

        curCommit.mergeBlob(stageArea);

        curCommit.saveCommit();

        //更新HEAD指向的commit
        updatePointerTo(HEAD, curCommit);
        //更新目前分支的commit
        updatePointerTo(join(BRANCH_DIR, readContentsAsString(curBranch)), curCommit);

        stageArea.clean();
        stageArea.saveStage();
        return curCommit;
    }

    /**
     * If the file is tracked in the current commit
     * stage it for removal and remove the file from the working directory
     * if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     */
    public static void rm(String fileName) throws IOException {
        Commit prevCommit = Commit.getCommit(HEAD);
        StageArea stageArea = readObject(STAGE_FILE, StageArea.class);
        //判断这个文件是否被暂存或者被跟踪
        boolean isStage = stageArea.containsBlob(fileName);
        boolean isTracked = prevCommit.containsBlob(fileName);

        if (!isStage && (!isTracked)) {
            System.out.println("No reason to remove the file.");
            return;
        }

        stageArea.rmBlob(fileName);
        stageArea.saveStage();

        File rmFile = join(CWD, fileName);

        //删除该文件
        if (rmFile.exists()) {
            restrictedDelete(fileName);
        }

    }

    /**
     * Starting at the current head commit,
     * display information about each commit backwards
     * along the commit tree until the initial commit
     */
    public static void log() {
        Commit curCommit = Commit.getCommit(HEAD);
        while (true) {
            curCommit.printCommitInfo();
            curCommit = curCommit.getParent(0);
            if (curCommit == null) {
                break;
            }
        }
    }

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     */
    public static void global_log() {
        List<String> commitNames = plainFilenamesIn(Commit.COMMIT_FOLDER);
        Commit curCommit;
        for (String commitName : commitNames) {
            curCommit = Commit.getCommit(commitName);
            curCommit.printCommitInfo();
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message,
     * one per line. If there are multiple such commits,
     * it prints the ids out on separate lines.
     */
    public static void find(String commitMsg) {
        List<String> commitNames = plainFilenamesIn(Commit.COMMIT_FOLDER);
        Commit curCommit;
        boolean found = false;
        for (String commitName : commitNames) {
            curCommit = Commit.getCommit(commitName);
            String msg = curCommit.getMessage();
            if (msg.equals(commitMsg)) {
                found = true;
                System.out.println(curCommit.getId());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Displays what branches currently exist,
     * and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     */
    public static void status() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory");
            System.exit(0);
        }
        //打印分支信息
        message("=== Branches ===");
        List<String> branchName = plainFilenamesIn(BRANCH_DIR);
        File curBranchFile = join(curBranch);
        String curBranch = readContentsAsString(curBranchFile);
        Collections.sort(branchName);
        for (String branch : branchName) {
            if (branch.equals(curBranch)) {
                message("*" + branch);
            } else {
                message(branch);
            }
        }
        message("");
        //打印add暂存的文件
        message("=== Staged Files ===");
        StageArea stage = readObject(STAGE_FILE, StageArea.class);
        List<String> addName = stage.getaddName();
        printStatus(addName);
        //打印删除文件的信息
        message("=== Removed Files ===");
        List<String> rmName = stage.getRmName();
        Commit HeadCommit = Commit.getCommit(HEAD);
        for (String rm : rmName) {
            if (HeadCommit.containsFile(rm)) {
                message(rm);
            }
        }
        message("");
        //打印任何工作区中与commit或stageArea中不同的情况
        message("=== Modifications Not Staged For Commit ===");
        message("");
        //打印没有commit且没有add的文件
        message("=== Untracked Files ===");
        message("");
    }

    private static void printStatus(List<String> list) {
        Collections.sort(list);
        for (String name : list) {
            message(name);
        }
        message("");
    }

    /**
     * 将HEAD中的该文件的版本覆盖到工作区
     */
    public static void checkoutHeadBlob(String headBlob) throws IOException {
        //读取HEAD中commit的id
        String commitId = readContentsAsString(HEAD);
        checkoutCommitBlob(commitId, headBlob);
    }

    /**
     * 将指定commit中的该文件的版本覆盖到工作区
     *
     * @param commitId   指定的commit的id
     * @param commitBlob 想要回溯文件的名称
     */

    public static void checkoutCommitBlob(String commitId, String commitBlob) throws IOException {
        Commit oldCommit = Commit.getCommit(commitId);
        if (oldCommit == null) {
            message("No commit with that id exists.");
            return;
        }
        Blob oldBlob = oldCommit.getBlob(commitBlob);
        if (oldBlob == null) {
            message("File does not exist in that commit.");
            return;
        }
        oldBlob.coverWorkFile();
    }

    /**
     * 将指定分支中的commit中储存的全部blob覆盖到工作区
     * 并将当前分支设置为改分支
     */
    public static void checkoutBranch(String branchName) throws IOException {
        File branch = join(BRANCH_DIR, branchName);
        //获得该分支内所储存的commit的id
        Commit branchCommit = Commit.getCommit(branch);
        Commit curCommit = Commit.getCommit(HEAD);
        //错误输入
        if (!branch.exists()) {
            message("No such branch exists.");
            return;
        } else if (readContentsAsString(curBranch).equals(branchName)) {
            // 2. the checked out branch is the current branch
            message("No need to checkout the current branch.");
            System.exit(0);
        } else if (hasUntrackedFile(curCommit, branchCommit)) {
            // 3. if a working file is untracked in the current branch and would be overwritten by the checkout
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        branchCommit.coverFile();
        updatePointerTo(HEAD, branchCommit);
        //跟新当前分支
        File curBranchFile = join(curBranch);
        writeContents(curBranchFile, branchName);

        StageArea stageArea = readObject(STAGE_FILE, StageArea.class);
        stageArea.clean();
        stageArea.saveStage();
    }


    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.
     */
    public static void creatBranch(String newBranchName) throws IOException {
        //创建一个新分支
        File newBranch = join(BRANCH_DIR, newBranchName);
        //如果文件已经存在报错返回
        if (newBranch.exists()) {
            message("A branch with that name already exists.");
            return;
        } else {
            newBranch.createNewFile();
        }
        Commit headCommit = Commit.getCommit(HEAD);
        updatePointerTo(newBranch, headCommit);
    }

    /**
     * Deletes the branch with the given name.
     */
    public static void rmBranch(String branchName) throws IOException {
        File rmBranch = join(BRANCH_DIR, branchName);
        if (!rmBranch.exists()) {
            message("A branch with that name does not exist.");
            return;
        }
        String curBranchName = readContentsAsString(curBranch);
        if (curBranchName.equals(branchName)) {
            message("Cannot remove the current branch.");
            return;
        }
        restrictedDelete(rmBranch);
    }

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     */
    public static void reset(String commitName) throws IOException {
        Commit curCommit = Commit.getCommit(commitName);
        for (String workFileName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            if (!curCommit.containsFile(workFileName)) {
                restrictedDelete(workFileName);
            }
        }
        curCommit.coverFile();
        //将当前HEAD设置为改commit
        updateBranch(HEAD, commitName);
        StageArea stageArea = readObject(STAGE_FILE, StageArea.class);
        stageArea.clean();
        stageArea.saveStage();
    }

    /**
     * Merges files from the given branch into the current branch.
     */
    public static void merge(String branchName) throws IOException {


        /* Failure checks */
        // FC1: uncommitted changes
        StageArea stageArea = new StageArea(STAGE_FILE);
        if (!stageArea.isEmpty()) {
            message("You have uncommitted changes.");
            return;
        }

        // FC2: If other branch does not exist, exit with error message
        File mergeBranch = join(BRANCH_DIR, branchName);
        //test-----------------------------------
        System.out.println(branchName);
        //---------------------------------
        if (!mergeBranch.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        //当前分支
        String curBranchName = readContentsAsString(curBranch);
        System.out.println(curBranchName);
        // FC3: merge with itself
        if (curBranchName.equals(branchName)) {
            checkoutBranch(branchName);
            message("Current branch fast-forwarded.");
            return;
        }
        //HEAD中的commit
        Commit headCommit = Commit.getCommit(HEAD);
        //目标branch最前端的commit

        File branchFile = join(BRANCH_DIR, branchName);
        Commit branchCommit = Commit.getCommit(readContentsAsString(branchFile));

        // FC4: untracked files
        if (hasUntrackedFile(headCommit, branchCommit)) {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }


        //找寻两个commit的分割点
        Commit splitCommit = Commit.findSplitPoint(headCommit, branchCommit);
        //FC5: split point is the current branch head || is the checked-out branch head
        if (splitCommit.getId().equals(branchCommit.getId())) {
            message("Given branch is an ancestor of the current branch.");
            return;
        } else if (splitCommit.getId().equals(headCommit.getId())) {
            message("Current branch fast-forwarded.");
            return;
        }

        Map<String, String> headMap = headCommit.getNameToBlob();
        Map<String, String> branchMap = branchCommit.getNameToBlob();
        Map<String, String> splitMap = splitCommit.getNameToBlob();
        Map<String, String> allMap = new HashMap<>(headMap);
        allMap.putAll(branchMap);
        allMap.putAll(headMap);
        allMap.putAll(splitMap);
        boolean conflicted = false;
        //遍历所有blob
        for (String blobName : allMap.keySet()) {
            Boolean existSplit = splitMap.containsKey(blobName);
            Boolean existHead = headMap.containsKey(blobName);
            Boolean existBranch = branchMap.containsKey(blobName);
            Boolean branchModify = modified(blobName, splitMap, branchMap);
            Boolean headModify = modified(blobName, headMap, branchMap);

            // a. existSplit && branchModify && !headModify -> update branchModify
            if (existSplit && !existBranch && !headModify) {
                // g. existSplit && !modifyHead && !existBranch -> remove(blobName) file
                stageArea.rmBlob(blobName);
            } else if (existSplit && !existHead && !branchModify) {
                // h. existSplit && !existHead && !branchModify -> remain removed
                continue;
            } else if (existSplit && existHead && !branchModify) {
                // a. existSplit && branchModify && !headModify -> update branchModify
                Blob tempBlob = branchCommit.getBlob(blobName);
                tempBlob.coverWorkFile();
                stageArea.addBlob(tempBlob);
            } else if (existSplit && headModify && !branchModify) {
                // b.existSplit && headModify && !branchModify
                continue;
            } else if (existSplit && headModify && branchModify) {
                // c.existSplit && headModify && branchModify (same modify)
                // d.existSplit && headModify && branchModify(diff modify)
                if (existBranch && existHead) {
                    conflicted = true;
                }
                Blob headBlob = headCommit.getBlob(blobName);
                String headContent = headBlob == null ? "" : headBlob.getFileContent();
                Blob branchBlob = branchCommit.getBlob(blobName);
                String branchContent = branchBlob == null ? "" : branchBlob.getFileContent();
                // write content str into file
                File tmp = join(CWD, blobName);
                writeContents(tmp, "<<<<<<< HEAD\n" + headContent + "=======\n" + branchContent + ">>>>>>>\n");
                Blob newBlob = new Blob(blobName, tmp);
                newBlob.coverWorkFile();
                stageArea.addBlob(newBlob);
                stageArea.saveStage();
            } else if (!existSplit && !branchModify && headModify) {
                // e. not in SPLIT && not otherHead && mod in curHead -> keep to curHead
                continue;
            } else if (!existSplit && !headModify && branchModify) {
                // f. not in SPLIT && not curHead && mod in otherHead -> update to otherHead
                Blob newBlob = branchCommit.getBlob(blobName);
                newBlob.coverWorkFile();
                stageArea.addBlob(newBlob);
            }
            //Make a merge commit
            String msg = String.format("Merged %s into %s.", branchName, readContentsAsString(curBranch));
            //If there's anything in the staging areas4
            if (!stageArea.isEmpty()) {
                Commit mergeCommit = commit(msg);
                mergeCommit.addParent(branchCommit);
            }

            if (conflicted) {
                System.out.println("Encountered a merge conflict.");
            }
        }

    }

    //判断是否含有未追踪文件
    public static boolean hasUntrackedFile(Commit headCommit, Commit branchCommit) throws IOException {
        for (String fileName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            if (!headCommit.containsFile(fileName) && branchCommit.containsFile(fileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the file in SUCCESSOR has been modified based onANCESTOR.
     */
    private static boolean modified(String blobName, Map<String, String> ancestor,
                                    Map<String, String> successor) {
        //equals ==ture ->not modified
        boolean mod = !ancestor.getOrDefault(blobName, "").equals(successor.getOrDefault(blobName, ""));
        return mod;
    }


}

