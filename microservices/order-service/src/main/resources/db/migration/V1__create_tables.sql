CREATE TABLE delivery_address
(
    delivery_address_id serial NOT NULL,
    street              varchar(255),
    city                varchar(255),
    state               varchar(255),
    country             varchar(255),
    postal_code         varchar(255),
    PRIMARY KEY (delivery_address_id)
);

CREATE TABLE product
(
    product_id serial  NOT NULL,
    price      decimal NOT NULL,
    name       varchar(255),
    PRIMARY KEY (product_id)
);

CREATE TABLE orders
(
    order_id            serial NOT NULL,
    delivery_address_id INT,
    PRIMARY KEY (order_id),

    CONSTRAINT fk_delivery_address
        FOREIGN KEY (delivery_address_id)
            REFERENCES delivery_address (delivery_address_id)
);

CREATE TABLE order_item
(
    order_item_id serial NOT NULL,
    product_id    INT,
    order_id      INT,
    amount        decimal,
    PRIMARY KEY (order_item_id),

    CONSTRAINT fk_product_id
        FOREIGN KEY (product_id)
            REFERENCES product (product_id),

    CONSTRAINT fk_order_id
        FOREIGN KEY (order_id)
            REFERENCES orders (order_id)
);