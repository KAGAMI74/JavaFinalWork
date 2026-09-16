package library;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 图书借阅管理系统 —— 命令行主程序。
 *
 * 启动流程：
 *   1. 创建 Library 对象，构造时自动从 data/books.dat 读回历史数据（序列化）；
 *   2. 启动后台自动保存线程（多线程，守护线程）；
 *   3. 循环打印菜单，用 Scanner 读取用户输入，调用 Library 的业务方法。
 */
public class Main {

    /** 自动保存间隔（秒）。 */
    private static final int SAVE_INTERVAL = 15;

    public static void main(String[] args) {
        Library library = new Library();

        // 启动后台自动保存线程（技术点三：多线程）
        Thread saver = new Thread(new AutoSaver(library, SAVE_INTERVAL), "auto-save");
        saver.setDaemon(true);
        saver.start();

        System.out.println("=========================================");
        System.out.println("       图书借阅管理系统 已启动");
        System.out.println("  数据文件：" + Library.DATA_FILE);
        System.out.println("  后台自动保存线程已启动，每 " + SAVE_INTERVAL + " 秒保存一次");
        System.out.println("=========================================");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addBook(scanner, library);
                    break;
                case "2":
                    showBooks(library.getBooks());
                    break;
                case "3":
                    searchBook(scanner, library);
                    break;
                case "4":
                    borrowBook(scanner, library);
                    break;
                case "5":
                    giveBackBook(scanner, library);
                    break;
                case "6":
                    removeBook(scanner, library);
                    break;
                case "7":
                    showStatistics(library);
                    break;
                case "8":
                    library.save();
                    System.out.println("数据已保存到 " + Library.DATA_FILE);
                    break;
                case "0":
                    library.save();
                    System.out.println("退出前已保存数据，再见！");
                    running = false;
                    break;
                default:
                    System.out.println("输入有误，请输入 0 ~ 8 之间的数字。");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("----------------- 功能菜单 -----------------");
        System.out.println(" 1. 添加图书            2. 查看全部图书");
        System.out.println(" 3. 查询图书            4. 借书");
        System.out.println(" 5. 还书                6. 删除图书");
        System.out.println(" 7. 统计信息            8. 立即保存数据");
        System.out.println(" 0. 退出系统");
        System.out.println("--------------------------------------------");
        System.out.print("请选择操作：");
    }

    /** 1. 添加图书。 */
    private static void addBook(Scanner scanner, Library library) {
        System.out.print("请输入书名：");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("书名不能为空，添加失败。");
            return;
        }
        System.out.print("请输入作者：");
        String author = scanner.nextLine().trim();
        System.out.print("请输入出版社：");
        String publisher = scanner.nextLine().trim();
        double price = readDouble(scanner, "请输入价格（元）：");
        if (price < 0) {
            System.out.println("价格不能为负数，添加失败。");
            return;
        }
        int id = library.addBook(title, author, publisher, price);
        library.save();
        System.out.println("添加成功，图书编号为 " + id + "。");
    }

    /** 2. 查看全部图书（由调用方传入集合，演示集合的遍历）。 */
    private static void showBooks(ArrayList<Book> books) {
        if (books.isEmpty()) {
            System.out.println("暂无图书。");
            return;
        }
        System.out.println("共 " + books.size() + " 条记录：");
        for (Book book : books) {
            System.out.println("  " + book.toLine());
        }
    }

    /** 3. 查询图书。 */
    private static void searchBook(Scanner scanner, Library library) {
        System.out.print("请输入书名或作者关键字（直接回车表示全部）：");
        String keyword = scanner.nextLine().trim();
        ArrayList<Book> result = library.search(keyword);
        System.out.println("查询到 " + result.size() + " 条结果：");
        showBooks(result);
    }

    /** 4. 借书。 */
    private static void borrowBook(Scanner scanner, Library library) {
        int id = readInt(scanner, "请输入要借阅的图书编号：");
        System.out.print("请输入借书人姓名：");
        String borrower = scanner.nextLine().trim();
        if (borrower.isEmpty()) {
            System.out.println("借书人不能为空，借阅失败。");
            return;
        }
        String message = library.borrow(id, borrower);
        library.save();
        System.out.println(message);
    }

    /** 5. 还书。 */
    private static void giveBackBook(Scanner scanner, Library library) {
        int id = readInt(scanner, "请输入要归还的图书编号：");
        String message = library.giveBack(id);
        library.save();
        System.out.println(message);
    }

    /** 6. 删除图书。 */
    private static void removeBook(Scanner scanner, Library library) {
        int id = readInt(scanner, "请输入要删除的图书编号：");
        if (library.remove(id)) {
            library.save();
            System.out.println("删除成功。");
        } else {
            System.out.println("没有编号为 " + id + " 的图书，删除失败。");
        }
    }

    /** 7. 统计信息。 */
    private static void showStatistics(Library library) {
        int total = library.getBooks().size();
        int borrowed = library.borrowedCount();
        System.out.println("---------- 统计信息 ----------");
        System.out.println("图书总数　：" + total + " 本");
        System.out.println("在馆可借　：" + (total - borrowed) + " 本");
        System.out.println("已经借出　：" + borrowed + " 本");
        System.out.println("借出率　　：" + library.borrowRate() + "%");
        System.out.println("馆藏总价值：" + String.format("%.2f", library.totalPrice()) + " 元");
        System.out.println("------------------------------");
    }

    /** 读取一个整数，输入不合法时返回 -1。 */
    private static int readInt(Scanner scanner, String tip) {
        System.out.print(tip);
        String text = scanner.nextLine().trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("请输入合法的数字。");
            return -1;
        }
    }

    /** 读取一个价格，输入不合法时返回 -1。 */
    private static double readDouble(Scanner scanner, String tip) {
        System.out.print(tip);
        String text = scanner.nextLine().trim();
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            System.out.println("请输入合法的数字。");
            return -1;
        }
    }
}
