package library;

/**
 * 技术点三（多线程）：后台自动保存线程。
 *
 * 主线程负责菜单交互，这个线程在后台每隔一段时间调用一次 Library.save()，
 * 把数据序列化写回文件，这样即使程序被意外关闭，数据也不会丢太多。
 * 它被设为守护线程，主程序退出时会一起结束，不会拦住程序退出。
 */
public class AutoSaver implements Runnable {

    private final Library library;
    private final int seconds;   // 自动保存间隔（秒）

    public AutoSaver(Library library, int seconds) {
        this.library = library;
        this.seconds = seconds;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                return;   // 被中断就结束线程
            }
            library.save();
            System.out.println("[自动保存线程] 已保存 " + library.getBooks().size() + " 本图书数据");
        }
    }
}
