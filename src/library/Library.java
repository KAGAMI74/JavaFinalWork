package library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * 图书管理业务类。
 *
 * 技术点一（集合）：用 ArrayList 保存全部图书和每一次查询的结果；
 * 技术点二（序列化）：启动时从 data/books.dat 读回数据，每次修改后写回文件。
 */
public class Library {

    /** 数据文件：全部图书都序列化保存在这一个文件里。 */
    public static final String DATA_FILE = "data" + File.separator + "books.dat";

    private final ArrayList<Book> books = new ArrayList<>();
    private int nextId = 1;

    public Library() {
        load();
    }

    // ---------------- 业务方法（集合操作） ----------------

    /** 添加图书，编号自动生成，返回新书编号。 */
    public int addBook(String title, String author, String publisher, double price) {
        books.add(new Book(nextId, title, author, publisher, price));
        return nextId++;
    }

    /** 按编号查找图书，找不到返回 null。 */
    public Book findById(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    /** 按关键字查询图书，结果放入一个新的集合返回。 */
    public ArrayList<Book> search(String keyword) {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.matches(keyword)) {
                result.add(book);
            }
        }
        return result;
    }

    /** 借书，返回提示信息。 */
    public String borrow(int id, String borrower) {
        Book book = findById(id);
        if (book == null) {
            return "没有编号为 " + id + " 的图书";
        }
        if (!book.borrow(borrower)) {
            return "《" + book.getTitle() + "》已经被借出，不能重复借阅";
        }
        return "借阅成功：《" + book.getTitle() + "》，借书人 " + borrower;
    }

    /** 还书，返回提示信息。 */
    public String giveBack(int id) {
        Book book = findById(id);
        if (book == null) {
            return "没有编号为 " + id + " 的图书";
        }
        if (!book.giveBack()) {
            return "《" + book.getTitle() + "》本来就在馆，无需归还";
        }
        return "归还成功：《" + book.getTitle() + "》已回到馆藏";
    }

    /** 删除图书，返回是否删除成功。 */
    public boolean remove(int id) {
        Book book = findById(id);
        if (book == null) {
            return false;
        }
        books.remove(book);
        return true;
    }

    /** 获取全部图书列表。 */
    public ArrayList<Book> getBooks() {
        return books;
    }

    // ---------------- 统计（用到 Math 常用类） ----------------

    /** 已借出的图书数量。 */
    public int borrowedCount() {
        int count = 0;
        for (Book book : books) {
            if (book.isBorrowed()) {
                count++;
            }
        }
        return count;
    }

    /** 借出率（百分比，用 Math.round 四舍五入取整）。 */
    public long borrowRate() {
        if (books.isEmpty()) {
            return 0;
        }
        return Math.round(borrowedCount() * 100.0 / books.size());
    }

    /** 全部图书的总价值。 */
    public double totalPrice() {
        double sum = 0;
        for (Book book : books) {
            sum += book.getPrice();
        }
        return sum;
    }

    // ---------------- 序列化保存与读取 ----------------

    /** 把图书集合整体序列化写入 data/books.dat。 */
    public synchronized void save() {
        File file = new File(DATA_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(books);
        } catch (IOException e) {
            System.out.println("保存数据失败：" + e.getMessage());
        }
    }

    /** 启动时读回数据；文件不存在（第一次运行）则写入初始图书。 */
    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            initSeedBooks();
            save();
            System.out.println("第一次运行，已自动写入 " + books.size() + " 本初始图书。");
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            books.clear();
            books.addAll((ArrayList<Book>) in.readObject());
            for (Book book : books) {
                if (book.getId() >= nextId) {
                    nextId = book.getId() + 1;   // 恢复编号，避免重启后重复
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("读取历史数据失败，将使用空数据启动：" + e.getMessage());
        }
    }

    /** 第一次运行时自动写入的几本书，方便直接演示。 */
    private void initSeedBooks() {
        addBook("Java 高级程序设计实战教程", "传智教育", "人民邮电出版社", 69.80);
        addBook("数据结构与算法分析", "Mark Allen Weiss", "机械工业出版社", 89.00);
        addBook("计算机网络（第 7 版）", "谢希仁", "电子工业出版社", 49.50);
        addBook("数据库系统概论", "王珊", "高等教育出版社", 45.00);
        addBook("操作系统概念", "Silberschatz", "机械工业出版社", 99.00);
    }
}
