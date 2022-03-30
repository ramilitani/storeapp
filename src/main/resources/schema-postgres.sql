create sequence IF NOT EXISTS hibernate_sequence start 1 increment 1;
create table IF NOT EXISTS product (id int8 not null, cluster varchar(255), product_desc varchar(255), product_ean int8, product_model varchar(255), product_season varchar(255), product_size varchar(255), product_sku varchar(255), region varchar(255), region_type varchar(255), store_name varchar(255), store_theme varchar(255), primary key (id));
create table IF NOT EXISTS available_field (id int8 not null, db_field_name varchar(255), name varchar(255), primary key (id));
create table IF NOT EXISTS cluster (id int8 not null, name varchar(255), primary key (id));
create table IF NOT EXISTS region (id int8 not null, name varchar(255), type varchar(255), cluster_id int8, primary key (id));
create table IF NOT EXISTS store (id int8 not null, name varchar(255), theme varchar(255), region_id int8, primary key (id));
ALTER TABLE region DROP CONSTRAINT IF EXISTS FKklawgpktd8wf00tvko9aui6pe;
alter table region add constraint FKklawgpktd8wf00tvko9aui6pe foreign key (cluster_id) references cluster;
ALTER TABLE store DROP CONSTRAINT IF EXISTS FKiecbc1b9m21semcf714lasyi5;
alter table store add constraint FKiecbc1b9m21semcf714lasyi5 foreign key (region_id) references region;

-- populate the AVAILABLE_FIELD table
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (1, 'Season', 'productSeason') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (2, 'Cluster', 'cluster') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (3, 'Region', 'region') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (4, 'Region Type', 'regionType') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (5, 'Model', 'productModel') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (6, 'Size', 'productSize') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (7, 'SKU', 'productSku') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (8, 'Store Name', 'storeName') ON CONFLICT (id) DO NOTHING;
INSERT INTO AVAILABLE_FIELD (ID, NAME, DB_FIELD_NAME) VALUES (9, 'Store Theme', 'storeTheme') ON CONFLICT (id) DO NOTHING;