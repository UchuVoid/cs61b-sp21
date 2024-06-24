package gitlet;

import java.io.IOException;

import static gitlet.Utils.message;
import static gitlet.Repository.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author UchuVoid
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {

//        args = new String[]{"merge","bugFix"};
//        args = new String[]{"checkout","bugFix"};
        /** 当没有输入指令时，报错 */
        if (args.length == 0) {
            message("please enter a command");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            /** java gitlet.Main init */
            case "init":
                try {
                    init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            /** java gitlet.Main add ... */
            case "add":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String fileName = args[1];
                add(fileName);
                break;
            /** java gitlet.Main commit ... */
            case "commit":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String msg = args[1];
                commit(msg);
                break;
            case "rm":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String rmFile = args[1];
                try {
                    rm(rmFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            //java gitlet.Main log
            case "log":
                if (args.length != 1) {
                    message("Incorrect operands.");
                }
                log();
                break;
            //java gitlet.Main global-log
            case "global-log":
                if (args.length != 1) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                global_log();
                break;
            //java gitlet.Main find [commit message]
            case "find":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String findMsg = args[1];
                find(findMsg);
                break;
            //java gitlet.Main status
            case "status":
                if (args.length != 1) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                status();
                break;
            /* Usage:
             * 1. java gitlet.Main checkout -- [file name]
             * 2. java gitlet.Main checkout [commit id] -- [file name]
             * 3. java gitlet.Main checkout [branch name] */
            case "checkout":
                if (args.length == 2) {
                    String checkoutBranch = args[1];
                    try {
                        checkoutBranch(checkoutBranch);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (args.length == 3) {
                    String checkoutFile = args[2];
                    if (!args[1].equals("--")) {
                        message("Incorrect operands.");
                        System.exit(0);
                    }
                    try {
                        checkoutHeadBlob(checkoutFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (args.length == 4) {
                    String checkoutCommit = args[1];
                    String checkoutFile = args[3];
                    if (!args[2].equals("--")) {
                        message("Incorrect operands.");
                        System.exit(0);
                    }
                    try {
                        checkoutCommitBlob(checkoutCommit, checkoutFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    message("Incorrect operands.");
                }
                break;
            case "branch":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String newBranchName = args[1];
                try {
                    creatBranch(newBranchName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "rm-branch":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String rmBranchName = args[1];
                try {
                    rmBranch(rmBranchName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "reset":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String resetMsg = args[1];
                break;
            case "merge":
                if (args.length != 2) {
                    message("Incorrect operands.");
                    System.exit(0);
                }
                String mergeBranch = args[1];
                try {
                    merge(mergeBranch);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                message("No command with that name exists.");
                System.exit(0);
        }
    }
}
