drop table if exists order;
-- drop table if exists order_item
-- create table IF NOT EXISTS order_item

create table order
(
    order_id serial NOT NULL,
    PRIMARY KEY (order_id),

    CONSTRAINT fk_delivery_address
        FOREIGN KEY (delivery_address_id)
            REFERENCES delivery_address (delivery_address_id),

    CONSTRAINT fk_order_item
        FOREIGN KEY (order_item_id)
            REFERENCES order_item (order_item_id)
);

-- in diagrama nu ar trebui sa avem OrderItem 0..1  -->  1 Product?
create table order_item
(
    order_item_id serial NOT NULL,
    amount serial,
    PRIMARY KEY( order_item_id),
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES product (product_id)
);

create table product
(
    product_id serial  NOT NULL,
    price      decimal NOT NULL,
    name       varchar(255),
    PRIMARY KEY (product_id)
);

create table delivery_address
(
    delivery_address_id serial NOT NULL,
    street              varchar(255),
    city                varchar(255),
    state               varchar(255),
    country             varchar(255),
    postal_code         varchar(255),
    PRIMARY KEY (delivery_address_id)
);