## 图书借阅系统

更新日期：11.14 -- 姚熙昂

### 1.项目设计

本项目旨在构建一个面向用户的图书借阅管理系统，实现如下功能：

1. 图书信息管理
2. 用户借阅与归还
3. 图书预约排队
4. 评论互动
5. 用户图书收藏夹

### 2.数据库设计

#### 2.1 用户表（user）

用于存储系统中所有用户的基本信息，包括登录账号、密码、姓名和联系方式。借阅、预约、评论、收藏等业务都通过 `user_id` 与该表关联。

```sql
CREATE TABLE user (
    id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username     VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录名',
    password     VARCHAR(255) NOT NULL COMMENT '密码(加密后)',
    role         ENUM('student', 'teacher', 'admin') NOT NULL COMMENT '用户角色',
    name         VARCHAR(50)  NOT NULL COMMENT '姓名',
    phone        VARCHAR(20)           COMMENT '手机号',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

------

#### 2.2 图书表（book）

用于存储系统中所有图书的基础信息，例如书名、作者、简介、ISBN、出版年份等，同时包含图书的总数量与库存数量。

```sql
CREATE TABLE book (
    id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    title         VARCHAR(200) NOT NULL COMMENT '书名',
    author        VARCHAR(100)          COMMENT '作者',
    description   TEXT                  COMMENT '简介',
    isbn          VARCHAR(30)           COMMENT 'ISBN编号',
    publish_year  INT                   COMMENT '出版年份',
    cover_url     VARCHAR(255)          COMMENT '封面图片URL',
    total         INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '图书总数量',
    stock         INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
    created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';
```

------

#### 2.3 分类标签表（tag）

用于存储系统中所有可用的分类 / 标签信息，例如"科幻""教育"等。图书可以通过中间表 `book_tag` 绑定多个标签，方便实现按标签筛选、分类浏览等功能。

```sql
CREATE TABLE tag (
    id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    name        VARCHAR(50)  NOT NULL COMMENT '标签名称，如科幻、教育',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类标签表';
```

------

#### 2.4 图书–分类标签映射表（book_tag）

用于建立图书与标签之间的**多对多关系**。一条记录代表本图书拥有某个标签。

```
CREATE TABLE book_tag (
    id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    book_id     BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
    tag_id      BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id)  ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书-分类标签映射表';
```

------

#### 2.5 评论表（comment）

用于存储用户对图书的评论信息。一条记录表示某个用户对某本书发表的一条评论。

```sql
CREATE TABLE comment (
    id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    user_id     BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
    book_id     BIGINT UNSIGNED NOT NULL COMMENT '评论图书ID',
    content     TEXT            NOT NULL COMMENT '评论内容',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
```

------

#### 2.6 收藏图书表（favorite）

用于记录用户收藏的图书信息。一条记录表示某个用户收藏了某本书，相当于个人书架功能。

```sql
CREATE TABLE favorite (
    id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id     BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    book_id     BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏图书表';
```

------

#### 2.7 借阅表（borrow）

用于记录用户的图书借阅信息，是一张非常核心的业务表。一条记录表示某个用户借阅了某本图书的一笔借阅记录，包含借出时间、应还时间、归还时间以及当前借阅状态等信息。

```sql
CREATE TABLE borrow (
    id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '借阅ID',
    user_id      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    book_id      BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
    borrow_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借书时间',
    due_time     DATETIME        NOT NULL COMMENT '应还时间',
    return_time  DATETIME                 COMMENT '实际归还时间（未归还则为NULL）',
    status       TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0=借出中，1=已归还，2=逾期',
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅表';
```

------

#### 2.8 预约表（reservation）

用于记录用户对图书的预约信息。当某本书已被借完时，用户可以进行预约；图书归还后，可以根据预约记录通知排队的用户。
`status` 字段用于表示预约的当前状态（如排队中、已取消、已完成等）。

```sql
CREATE TABLE reservation (
    id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    user_id      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    book_id      BIGINT UNSIGNED NOT NULL COMMENT '图书ID',
    status       TINYINT         NOT NULL DEFAULT 0 COMMENT '状态：0=排队中，1=已取消，2=已完成',
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';
```

### 3.接口设计

注意：图书表中有一个字段为cover_url，用于存储该图书的url地址，这个字段可以先置为空，前端可以用本地图片填充，后端可以先不实现。

#### 3.1 用户模块

##### 3.1.1 用户登录

已经实现

##### 3.1.2 用户注册

已经实现

#### 3.2 图书管理

##### 3.2.1 创建图书

已经实现，该功能只能支持role为teacher和admin的用户。

##### 3.2.2 获取所有图书

获取所有图书相当于查询，需要添加几个查询字段，即：书名、作者、ISBN编号和图书标签，图书标签为一个字符串，比如“科技”，指明标签表的name字段，由于图书众多，这里考虑做分页，也就是分页查询。前端传过来 **查询数量limit** 和 **查询偏移offset** 两个参数，后端根据这两个参数进行查询。

如果前端没有传入任何参数，默认返回所有数据。

**注意！！！**：在每次查询过后，在返回的数据最后加上total字段（查询的数据的总数，eg：如果我查询作者为小明的书籍，total就为小明名下的书籍数量）

返回数据格式：

```json
{
    "status": "ok",
    "message": "",
    "data": {
        "result": [
            {
                "id": 1,
                "title": "计算机科学导论",
                "author": "张三",
                "description": "一本关于计算机科学基础的书籍。",
                "isbn": "978-7-123-45678-9",
                "publish_year": 2020,
                "cover_url": "...",
                "total": 100,
                "stock": 50,
                "created_at": "2023-01-01 10:00:00",
                "updated_at": "2023-01-10 15:00:00"
            },
            {
                "id": 2,
                "title": "人工智能简史",
                "author": "李四",
                "description": "介绍人工智能发展历史的书籍。",
                "isbn": "978-7-123-98765-4",
                "publish_year": 2021,
                "cover_url": "...",
                "total": 200,
                "stock": 120,
                "created_at": "2023-02-01 11:00:00",
                "updated_at": "2023-02-05 16:00:00"
            }
        ],
        "total": 2
    }
}
```

##### 3.2.3 获取单个图书

这里获取单个图书需要进行连接查询，返回该图书基本信息，然后加上该图书所属分类标签以及该图书的评论，评论里面又包含发表该评论的用户信息。

返回数据格式：

```json
{
    "status": "ok",
    "message": "",
    "data": {
        "book": {
            "id": 1,
            "title": "计算机科学导论",
            "author": "张三",
            "description": "一本关于计算机科学基础的书籍。",
            "isbn": "978-7-123-45678-9",
            "publish_year": 2020,
            "cover_url": "...",
            "total": 100,
            "stock": 50,
            "created_at": "2023-01-01 10:00:00",
            "updated_at": "2023-01-10 15:00:00"
        },
        "tags": [
            {
                "id": 3,
                "name": "计算机",
                "created_at": "2022-11-01 12:00:00"
            },
            {
                "id": 7,
                "name": "科学",
                "created_at": "2022-11-05 11:10:00"
            }
        ],
        "comments": [
            {
                "id": 12,
                "content": "内容通俗易懂，非常适合入门。",
                "created_at": "2023-03-01 09:30:00",
                "user": {
                    "id": 5,
                    "name": "李四"
                }
            },
            {
                "id": 13,
                "content": "讲解很清晰，但部分章节略显简单。",
                "created_at": "2023-03-03 14:20:00",
                "user": {
                    "id": 8,
                    "name": "王五"
                }
            }
        ]
    }
}
```

##### 3.2.4 更新图书

已经实现，就是修改图书的基本信息。该功能只能支持role为teacher和admin的用户。

##### 3.2.5 删除图书

已经实现。该功能只能支持role为teacher和admin的用户。

#### 3.3 图书借阅

##### 3.3.1 获取所有借阅书籍

这里的要求和返回格式和3.2.2 获取所有图书类似，然后需要根据用户基本信息来返回数据，多了一个连接查询（根据借阅书籍表的book_id来做查询）。

##### 3.3.1 借书

已经实现。

##### 3.3.2 还书

由于添加了预约的功能，所以还书之后需要进行判断：

1. 如果归还之后数量>1，说明预约表中没有该书籍，不需要进行处理。
2. 如果归还之后数量=1，说明预约表当中可能会存在该书籍的预约信息，所以需要在预约表当中搜索该书籍，若发现，则选择时间最早的一个（且status为0）创建一条借书记录，并把该预约记录的status设为2（已完成）。

#### 3.4 图书收藏

##### 3.4.1 获取所有收藏图书

要求和3.3.1、3.2.2类似。

##### 3.4.2 添加收藏图书

类似。

##### 3.4.3 删除收藏图书

类似。

#### 3.5 图书预约

图书预约功能用于在图书库存为 0 时，让用户进入预约队列。当有读者归还图书后，系统可以根据预约时间先后顺序依次通知排队用户，也就是为预约表中该书籍最早预约的用户分配书籍。

预约表已在 2.8 中定义：`reservation`，状态 `status`：

- `0 = 排队中`
- `1 = 已取消`
- `2 = 已完成`

##### 3.5.1 获取所有预约书籍

要求和3.3.1、3.2.2、3.4.1类似，根据用户信息和查询信息进行获取。

##### 3.5.2 预约图书

类似。

##### 3.5.3 取消预约

类似。

### 4.前端概述

路由结构如下：

```bash
/                     <- 一级路由：重定向至 /login 或 /library
/login                <- 一级路由：登录 / 注册页面

/library              <- 一级路由：主布局（MainLayout）
├── /library/home             <- 二级路由：系统首页
├── /library/book             <- 二级路由：图书管理
├── /library/borrow           <- 二级路由：借阅管理
├── /library/appointment      <- 二级路由：预约管理
├── /library/favorite         <- 二级路由：图书收藏
└── /library/personal         <- 二级路由：个人信息
```

登录注册页面可以加一个 `isRegister` 变量来判断是登录还是注册，通过按钮进行表单切换。

系统首页涉及拓展功能，可以先写个欢迎标语。

### 5. 拓展功能

也就是数据统计的功能，有时间的话可以做一下，放在前端的home页面来显示，下面是一些想法：

1. 统计借阅数量最多的10本书籍（通过在借阅表中查询获得，前端通过表显示）。
2. 统计借阅数量最多的5名用户。
3. 图书推荐（根据用户最近一次借阅记录的书籍类别标签，然后搜索几本标签相同的书籍进行推荐）。





































