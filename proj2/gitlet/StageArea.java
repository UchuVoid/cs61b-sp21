package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/**
 * 保存修改后文件的版本
 *
 * @author UchuVoid
 */

public class StageArea implements Serializable {
    private final File STAGE_FILE;
    //储存add后的文件
    private TreeMap<String, String> nameToBlob;
    //储存要删除的文件
    private List<String> rmFile;

    public StageArea(File stageFile) {
        rmFile = new ArrayList<>();
        STAGE_FILE = stageFile;
        nameToBlob = new TreeMap<>();
    }

    /**
     * 将add的文件的文件名和hash值储存到暂存区
     */
    public void addBlob(Blob addBlob) {
        /** 判断暂存区中是否含有该版本文件
         * 如果有退出*/
        if (nameToBlob.get(addBlob.getFileName()) != null) {
            return;
        }
        nameToBlob.put(addBlob.getFileName(), addBlob.getId());
    }

    /**
     * 添加要删除的文件
     * 如果之前缓存了从缓存区删除
     */
    public void rmBlob(String rmName) {
        if (nameToBlob.get(rmName) != null) {
            nameToBlob.remove(rmName);
        } else {
            rmFile.add(rmName);
        }
    }

    /**
     * 将add后的暂存区保存到stageArea文件中
     */
    public void saveStage() {
        File stageFile = join(STAGE_FILE);
        writeObject(stageFile, this);
    }

    //清除缓存区中修改的文件
    public void clean() {
        nameToBlob.clear();
        rmFile.clear();
    }

    //返回add文件的名称
    public List<String> getaddName() {
        List<String> addName = new ArrayList<>();
        addName.sort(Comparator.naturalOrder());
        for (String name : nameToBlob.keySet()) {
            addName.add(name);
        }
        return addName;
    }

    public List<String> getRmName() {
        rmFile.sort(Comparator.naturalOrder());
        return rmFile;
    }

    public String getBlobId(String fileName) {
        return nameToBlob.get(fileName);
    }

    public boolean containsBlob(String fileName) {
        return nameToBlob.containsKey(fileName);
    }

    public boolean isEmpty() {
        if (nameToBlob.isEmpty() && rmFile.isEmpty()) {
            return true;
        }
        return false;
    }

    public TreeMap<String, String> getNameToBlob() {
        return nameToBlob;
    }

    public List<String> getRmFile() {
        return rmFile;
    }
}
