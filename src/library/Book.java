package library;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图书实体类。
 * 实现 Serializable 接口后才能被对象序列化保存到文件中（技术点二）。
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;          // 图书编号（自动生成）
    private String title;          // 书名
    private String author;         // 作者
    private String publisher;      // 出版社
    private double price;          // 价格（元）
    private boolean borrowed;      // 是否已借出
    private String borrower;       // 借书人姓名
    private Date borrowDate;       // 借书日期

    public Book(int id, String title, String author, String publisher, double price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.borrowed = false;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public String getBorrower() {
        return borrower;
    }

    /** 借书：已借出的书不能再借，返回 false。 */
    public boolean borrow(String borrower) {
        if (borrowed) {
            return false;
        }
        this.borrowed = true;
        this.borrower = borrower;
        this.borrowDate = new Date();
        return true;
    }

    /** 还书：未借出的书不能还，返回 false。 */
    public boolean giveBack() {
        if (!borrowed) {
            return false;
        }
        this.borrowed = false;
        this.borrower = null;
        this.borrowDate = null;
        return true;
    }

    /** 关键字匹配书名或作者（技术点：String 常用类）。 */
    public boolean matches(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String key = keyword.trim();
        return title.contains(key) || author.contains(key);
    }

    /** 一行文本显示图书信息。 */
    public String toLine() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (borrowed) {
            return String.format("[%d]《%s》%s  %s  %.2f元  已借出（%s  %s）",
                    id, title, author, publisher, price, borrower, fmt.format(borrowDate));
        }
        return String.format("[%d]《%s》%s  %s  %.2f元  在馆可借",
                id, title, author, publisher, price);
    }
}
