 # sql
 
 - 1.商品类目表
 ```sql
 create table t_category (
    id varchar(255) not null,
    name varchar(255) not null,
    detail text not null,
    hot tinyint not null,
    create_time timestamp not null default current_timestamp, 
    update_time timestamp not null default current_timestamp on update current_timestamp, 
    primary key(id)
 );
 ```
 
 - 2.商品表
 ```sql
create table t_product (
  id varchar(255) not null,
  name varchar(255) not null,
  introduction text not null,
  detail text not null,
  price decimal(8,2) not null,
  stock int not null default 0,
  state int not null default 0 comment '商品默认为下架状态',
  main_picture varchar(255) not null default '',
  sub_picture varchar(255) not null default '',
  category_id varchar(255) not null,
  create_time timestamp not null default current_timestamp, 
  update_time timestamp not null default current_timestamp on update current_timestamp, 
  primary key(id)
);
```

- 3.订单详情表
```sql
create table t_order_item (
  id varchar(255) not null,
  order_id varchar(255) not null,
  product_id varchar(255) not null,
  quantity int not null comment '商品数量',
  amount decimal(8, 2) not null comment '订单项总额',
  primary key(id)
);
```

- 4.订单表
```sql
create table t_order (
  id varchar(255) not null,
  order_no varchar(255) not null,
  total decimal(8, 2) not null,
  state int not null default 0 comment '订单状态默认未发货',
  user_id varchar(255) not null,
  shipping_id varchar(255) not null,
  create_time timestamp not null default current_timestamp, 
  update_time timestamp not null default current_timestamp on update current_timestamp, 
  primary key(id),
  unique key(order_no)
);
```

- 5.用户表
```sql
create table t_user (
  id varchar(255) not null,
  user_name varchar(255) not null,
  password varchar(255) not null,
  email varchar(255) not null,
  mobile varchar(255) not null,
  state int not null,
  create_time timestamp not null default current_timestamp, 
  update_time timestamp not null default current_timestamp on update current_timestamp, 
  primary key(id)
);
```

- 6.管理员表
```sql
create table t_admin (
  id varchar(255) not null,
  admin_name varchar(255) not null,
  password varchar(255) not null,
  root tinyint not null default 0,
  create_time timestamp not null default current_timestamp, 
  primary key(id)
);
```

- 7.物流信息表
```sql
create table t_shipping (
  id varchar(255) not null,
  user_id varchar(255) not null,
  receiver_name varchar(255) not null,
  receiver_mobile varchar(255) not null,
  receiver_province varchar(255) not null,
  receiver_city varchar(255) not null,
  receiver_district varchar(255) not null,
  receiver_address varchar(255) not null,
  receiver_zip varchar(255) not null,
  create_time timestamp not null default current_timestamp, 
  update_time timestamp not null default current_timestamp on update current_timestamp, 
  primary key(id)
);
```

- 8.支付信息表
```sql
create table t_pay_info (
  id varchar(255) not null,
  user_id varchar(255) not null,
  order_no varchar(255) not null,
  pay_platform int not null,
  platform_number varchar(255) not null,
  platform_status varchar(255) not null,
  create_time timestamp not null default current_timestamp, 
  update_time timestamp not null default current_timestamp on update current_timestamp, 
  primary key(id)
);
```