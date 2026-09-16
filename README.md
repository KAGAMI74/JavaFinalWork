# 图书借阅管理系统

Java 高级程序设计（软件设计及开发 II）课程大作业。仿照教材最后一章综合实训的写法设计并实现了一个
**图书借阅管理系统**，使用 CMD 菜单方式进行功能选择，数据用对象序列化保存在本地文件中，无需安装数据库。

## 技术点（7 选 3）

| # | 技术点 | 用在哪里 | 关键类 / 方法 |
| --- | --- | --- | --- |
| 1 | 集合 | `ArrayList<Book>` 保存全部图书，查询结果也用集合保存 | `ArrayList`、`for-each` 遍历、`contains` |
| 2 | 序列化 | 启动时读回数据、每次修改后写回 `data/books.dat` | `Serializable`、`ObjectOutputStream`、`ObjectInputStream` |
| 3 | 多线程 | 后台守护线程每 15 秒自动保存一次数据 | `Thread`、`Runnable`、`Thread.sleep`、`setDaemon(true)` |

另外按要求使用了常用类：`String`（输入校验、`String.format` 格式化输出）、`Date` + `SimpleDateFormat`
（记录并显示借书日期）、`Math`（`Math.round` 计算借出率）。

全部代码只依赖 JDK 标准库，未使用 SSH、SpringBoot 等任何框架。

## 目录结构

```
Java大作业源代码
├── .idea/                 IDEA 工程配置（已配好 JDK 与运行配置）
├── Java大作业源代码.iml    模块文件
├── src/library
│   ├── Book.java         实体类，实现 Serializable
│   ├── Library.java      业务类：集合管理 + 序列化读写 + 统计
│   ├── AutoSaver.java    多线程：后台定时保存
│   └── Main.java         命令行菜单入口
├── data/books.dat        序列化数据文件（自动生成）
└── README.md
```

## 运行方式

IDEA 中直接运行 `src/library/Main.java`，或者选择运行配置 **Main**（已内置）。

项目 SDK 已在 `.idea/misc.xml` 中显式指定，打开工程即为「JDK 25」，
不会出现「Project SDK is not defined / 找不到 JDK」之类的报错。
如果本机装的是其它版本 JDK，只需 `File → Project Structure → SDK`
改成自己的版本即可（代码只用到 Java 8 语法和标准库，8 及以上都能编译）。

命令行：

```bash
javac -encoding UTF-8 -d out/production/Java大作业源代码 src/library/*.java
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dstdin.encoding=UTF-8 -cp out/production/Java大作业源代码 library.Main
```

## 功能清单

| 序号 | 功能 | 说明 |
| --- | --- | --- |
| 1 | 添加图书 | 输入书名、作者、出版社、价格，编号自动生成 |
| 2 | 查看全部图书 | 按编号顺序显示全部图书及借阅状态 |
| 3 | 查询图书 | 按书名或作者关键字模糊查询 |
| 4 | 借书 | 登记借书人与借书日期，已借出的书拒绝重复借阅 |
| 5 | 还书 | 归还后图书恢复为在馆可借 |
| 6 | 删除图书 | 按编号删除 |
| 7 | 统计信息 | 图书总数、在馆可借、已经借出、借出率、馆藏总价值 |
| 8 | 立即保存数据 | 手动触发一次序列化保存 |
| 0 | 退出系统 | 退出前自动保存一次 |

## 数据说明

- 第一次运行会自动写入 5 本初始图书，启动后即可直接查看、查询和统计。
- 全部数据序列化保存在 `data/books.dat`，重启后自动恢复，编号不会重复。
- 恢复初始状态：停止程序，删除 `data/books.dat`，再重新运行。
