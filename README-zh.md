[English][1] | 中文

[1]: https://github.com/ineedahouse/IDEA-Plugin-ResolvingMapper/blob/master/README-zh.md

# IDEA-Plugin-ResolvingMapper

这是一个IDEA插件，可以解析Mapper.xml文件中所有的表名与存储过程名称，同时支持单个文件和多个文件。你不仅可以在展示这些信息在你的IDEA中，你也可以将其导出到指定路径。如果您有问题或者建议，请和我们联系。

## 支持标签

我们目前支持如下标签：

`select`、`insert`、`update`、`delete`、`sql`、`if`

## 使用方法

#### 安装

你可以通过两种方式安装：

1. File-->Settings-->Plugins-->Marketplace-->搜索Resolving Mapper-->点击install
2. 首先你需要[下载插件文件][2]，然后打开IDEA 并按如下顺序操作， File-->Settings-->Plugins-->  点击installed旁边的设置按钮-->Install Plugin from Disk-->选择你本地下载的文件-->点击restart

tips: 在这过程中你可能会遇到版本不兼容的问题，尽可能使用IDEA较新版本。

#### 在IDEA里展示表和存储过程的信息

![image]( https://github.com/ineedahouse/markdownPhoto/blob/main/IDEA-Plugin-ResolvingMapper/showTableAndProc.gif)

#### 将表和存储过程的信息导出到本地

![image]( https://github.com/ineedahouse/markdownPhoto/blob/main/IDEA-Plugin-ResolvingMapper/exportTableAndProc.gif)

## 开发环境

IntelliJ IDEA 2019.2.3
Build #IU-192.6817.14, built on September 24, 2019
Runtime version: 1.8.0_221-b11 amd64
VM: Java HotSpot(TM) 64-Bit Server VM by Oracle Corporation
Windows 7 6.1

### *License*

 [Apache 2.0 license](LICENSE).

[2]:https://github.com/ineedahouse/IDEA-Plugin-ResolvingMapper/releases/tag/v1.1.3