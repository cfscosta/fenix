alter table DEGREE_INFO add column NAME longtext;
update DEGREE_INFO DI, DEGREE D set DI.NAME = concat('pt', LENGTH(NOME), ':', NOME, 'en', LENGTH(NAME_EN), ':', NAME_EN) WHERE DI.KEY_DEGREE=D.ID_INTERNAL;