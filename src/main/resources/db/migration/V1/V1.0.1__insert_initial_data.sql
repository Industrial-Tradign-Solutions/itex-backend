/*DATA INICIAL DE ROLES*/
INSERT INTO t_roles (id,name, description, is_active, created_at, is_editable)
    VALUES ('1b5c43fd-d711-4557-b2dd-f29a00415ee6','SUPER USER', 'This role manages the entire system without any restrictions', TRUE, now(), false);
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*DATA INICIAL DE DEPARTMENTS*/
INSERT INTO t_departments (id, name, description, is_active, created_at, is_client_info)
    VALUES ('3470959b-7f1f-4ec8-a03d-bd9a29dedcb0', 'IT', 'Systems and Technology Department', TRUE, now(), false);
INSERT INTO t_departments (id, name, description, is_active, created_at, is_client_info)
    VALUES ('e25ba53d-0d35-473e-b8da-99eb84c06c35', 'IP', 'Industrial Purchases Department', TRUE, now(), true);
INSERT INTO t_departments (id, name, description, is_active, created_at, is_client_info)
    VALUES ('b4ceccbc-3de5-419f-9cb7-58633eba51ce', 'RM', 'Raw Materials Department', TRUE, now(), true);
INSERT INTO t_departments (id, name, description, is_active, created_at, is_client_info)
    VALUES ('38dd94f1-6583-4a00-b290-9ec2e630574f', 'LO', 'Logistics Operations Department', TRUE, now(), true);
INSERT INTO t_departments (id, name, description, is_active, created_at, is_client_info)
    VALUES ('3febfa66-2948-4b5a-af30-81dbc9402045', 'IF', 'Inland Freight Department', TRUE, now(), true);
INSERT INTO t_departments (id, name, description, is_active, created_at, is_client_info)
    VALUES ('72214552-2ddc-417f-b58e-05f28eabc80f', 'AC', 'Accounting Department', TRUE, now(), true);
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*CREACION DE UNICO SUPER USUARIO*/
INSERT INTO t_users (id, created_at, is_active, name, last_name, password, email, role_id, is_pass_changed, department_id, username, title, extension)
    VALUES ('7b0d5ef2-f6dd-4a29-ae0c-726f723419fc',
            now(),
            TRUE,
            'Super',
            'Usuario',
            '$2a$10$YpppG3/2bLExEXgeazI8h.KNwuaY4hc/0rgY/3mH4A5nDk.DRwg.y',
            'super-user@itradingsolutions.com',
            '1b5c43fd-d711-4557-b2dd-f29a00415ee6',
            FALSE,
            '3470959b-7f1f-4ec8-a03d-bd9a29dedcb0',
            'super',
            'Systems Administrator',
            '110'
           );
/* contraseña inicial es 123456, hay que cambiarla*/
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Configuracion de modulos iniciales para administracion*/
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (1000, now(), true, 'System administration category', '', 'Administration', '/p/administration', null, TRUE, 100);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (1001, now(), true, 'User management module', 'pi pi-user', 'Users', '/p/administration/users', 1000, FALSE, 1);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (1002, now(), true, 'Role management module', 'pi pi-key', 'Permissions', '/p/administration/roles', 1000, FALSE, 2);
/*--------------------------------------------------------*/

/*Configuracion de modulos maestros*/
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (2000, now(), true, 'System masters category', '', 'Masters', '/p/masters', null, TRUE, 90);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (2001, now(), true, 'Departments management module', 'pi pi-building', 'Departments', '/p/masters/departments', 2000,FALSE, 1);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (2002, now(), true, 'Locations management module', 'pi pi-flag', 'Locations', '/p/masters/locations', 2000, FALSE, 2);
INSERT INTO t_menus (id, created_at, is_active, description, icon, name, url, main_menu_id, is_main_option, position)
    VALUES (2003, now(), true, 'Industries management module', 'pi pi-bolt', 'Industries', '/p/masters/industries', 2000, FALSE, 8);
/*--------------------------------------------------------*/

/*Acciones modulo usuarios*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1001001, 'Create User', 'Allows you to create a user', 1001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1001002, 'Update User', 'Allows you to update a user', 1001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1001003, 'Enable User', 'Allows you to enable a user', 1001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1001004, 'Disable User', 'Allows you to disable a user', 1001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1001005, 'Reset user password','Allows you to resend a default password to the user so that he/she can access the system again', 1001, true,now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1001006, 'Close All Sessions', 'Sends a notification to users that the system will be unavailable within the next 5 minutes', 1001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Acciones modulo roles*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1002001, 'Create Role', 'Allows you to create a Role', 1002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1002002, 'Update Role', 'Allows you to update a Role', 1002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1002003, 'Enable Role', 'Allows you to enable a Role', 1002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1002004, 'Disable Role', 'Allows you to disable a Role', 1002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1002005, 'Update Role Actions', 'Allows to update the actions that a user can do in the system', 1002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (1002006, 'Update Role Menus', 'Allows to update the modules to which a user has access', 1002, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Acciones modulo departamentos*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2001001, 'Create Department', 'Allows you to create a Department', 2001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2001002, 'Update Department', 'Allows you to update a Department', 2001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2001003, 'Enable Department', 'Allows you to enable a Department', 2001, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2001004, 'Disable Department', 'Allows you to disable a Department', 2001, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Acciones modulo locations*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2002101, 'Create Country', 'Allows you to create a Country', 2002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2002102, 'Update Country', 'Allows you to update a Country', 2002, true, now());

INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2002201, 'Create State', 'Allows you to create a State', 2002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2002202, 'Update State', 'Allows you to update a State', 2002, true, now());

INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2002301, 'Create City', 'Allows you to create a City', 2002, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2002302, 'Update City', 'Allows you to update a City', 2002, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Acciones modulo industries*/
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2003001, 'Create Industry', 'Allows you to create a Industry', 2003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2003002, 'Update Industry', 'Allows you to update a Industry', 2003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2003003, 'Enable Industry', 'Allows you to enable a Industry', 2003, true, now());
INSERT INTO t_actions (id, name, description, menu_item_id, is_active, created_at)
    VALUES (2003004, 'Disable Industry', 'Allows you to disable a Industry', 2003, true, now());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*Data Inicial Industries*/
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('db1b60a6-7889-4252-b299-6504d1486d81', 'AGRICULTURE', 'Production of food and raw materials through the cultivation of crops and grains, including improvement techniques to optimize yield and sustainability.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('24184466-47cf-44a8-bb8f-905fd1a23559', 'LIVESTOCK', 'Breeding animals for meat, milk, leather, and other derived products, contributing to food supply and the textile industry.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('9d90cd01-5700-4bfa-a726-9043cdd1f60f', 'FORESTRY', 'Management and cultivation of forests to obtain wood, paper, and derived products, fostering environmental conservation.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('a4aae0ab-9da7-4cb3-9b5a-61b45adefd7a', 'FISHING AND AQUACULTURE', 'Catching fish and controlled cultivation of aquatic species for human consumption, contributing to food security.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('4c62b891-6d3e-4067-8744-704de6dc8265', 'MINING', 'Extraction of minerals and metals from the earth for use in manufacturing, technology, construction, and energy', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('e033fb6d-9cba-4e88-ab3e-d3d4af77bfa4', 'OIL AND GAS', 'Exploration, extraction, and refining of hydrocarbons used as energy sources and in chemical products.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('e49b8376-1dda-4786-a387-2c5691997667', 'FOOD INDUSTRY', 'Processing and manufacturing of food for human consumption, from basic products to prepared foods.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('ea869939-ff4b-49b6-80f7-1ee060bcb0fa', 'TEXTILE AND APPAREL', 'Production of fabrics and clothing, encompassing everything from spinning and weaving to fashion design.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('89aa568f-5396-4f45-b35a-a31f517e1a46', 'CHEMICAL INDUSTRY', 'Production of chemicals for various sectors, including pharmaceuticals, construction materials, and cleaning products.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('5cf90381-7655-4b6a-b10f-05d303360212', 'PHARMACEUTICAL INDUSTRY', 'Research, development, and production of medicines and health products for treating illnesses.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('04a757b8-ad5e-40ba-aa25-02c3a492b604', 'METALLURGY', 'Processing of metals to create industrial products, from machinery components to tools and vehicles', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('8760a49a-a19a-49b5-9e7c-86c51e2ea1b4', 'STEEL INDUSTRY', 'Production of steel and iron, essential in construction, machinery, automotive, and various industrial applications.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('0b3489a5-1efd-458b-8850-e8be9f083ce5', 'CONSTRUCTION', 'Building infrastructure and housing, including commercial, industrial, and residential projects', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('6ee03d6e-b847-43f2-a355-53fd3e61b279', 'AUTOMOTIVE', 'Design, manufacturing, and marketing of vehicles, including cars, trucks, and motorcycles.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('1fbeddfe-9993-4c51-98ab-6828a3c20659', 'ELECTRONICS', 'Manufacturing of electronic devices, such as computers, phones, and household appliances, used across all sectors.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('89f63c9d-16e4-4d73-870c-0a1518889438', 'MACHINERY AND INDUSTRIAL EQUIPMENT', 'Production of machinery for various industries, from agriculture to manufacturing and construction.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('d7ace6a2-fcdd-407a-a2cb-0b04e022ee4c', 'PLASTICS AND RUBBER INDUSTRY', 'Manufacturing of plastic and rubber products, used in packaging, automotive, construction, and consumer goods.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('4e7e81ec-f5f9-46a1-854c-cbd2094bff4c', 'PAPER AND CARDBOARD INDUSTRY', 'Production of paper and cardboard for use in packaging, publications, and consumer products', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('91d9cb03-edd3-4afc-9d74-aaa2f0491b27', 'ELECTRIC POWER', 'Generation, distribution, and supply of electric energy for residential, commercial, and industrial use.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('4e730807-99a1-473f-a2f0-0fb91033750b', 'FINANCIAL SERVICES', 'Financial Services', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('d230899e-1ba4-4921-b2cd-f1b95d19c387', 'TRADE AND DISTRIBUTION', 'Commercialization and distribution of consumer goods, encompassing both retail and wholesale trade.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('67c86fd4-4df8-4e66-a9af-3da89a967c06', 'TELECOMMUNICATIONS', 'Provision of communication services, such as mobile telephony, internet, and communication networks.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('c4a02329-d47f-42ea-b6b4-5757c703da1c', 'TOURISM AND HOSPITALITY', 'Accommodation, entertainment, and transportation services for tourists and travelers.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('0a7d23d0-cdce-4ba0-8f54-42425d03d3b4', 'TRANSPORTATION AND LOGISTICS', 'Movement of goods and people, encompassing land, air, and sea transport and supply chain management.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('05a76077-cecf-4332-a934-4c5632790141', 'HEALTH AND WELLNESS', 'Medical, fitness, and wellness services aimed at improving quality of life and health.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('9c8c460b-e8a0-4e5e-a101-b0c680f2e8b3', 'EDUCATION', 'Provision of educational services, from basic to advanced levels, including technical and professional training.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('e7200655-3a02-4f16-b0e4-a3fda2f68fc5', 'MEDIA AND COMMUNICATIONS', 'Production and distribution of informative and entertainment content through channels like TV, radio, and the internet.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('e8d8982d-5266-4964-9525-a4e5fd9f0c03', 'CONSULTING', 'Professional advisory services in areas like business, finance, technology, and corporate management', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('db4a5a90-e50e-43e1-b830-a63d7c7c6561', 'RESEARCH AND DEVELOPMENT (R&D)', 'Innovation in products, processes, and services through scientific and technical research.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('42e650d0-3c89-48fb-a3b3-dfd8c2d03d9c', 'INFORMATION TECHNOLOGY (IT)', 'Development and management of computing and technological systems, including software, hardware, and cybersecurity.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('5a890bf6-d649-4105-b75c-2aab252445c7', 'BIOTECHNOLOGY', 'Application of biological processes for product development in areas like medicine, agriculture, and environmental conservation.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('37de4921-2c2e-41a9-9998-df1c94c98754', 'NANOTECHNOLOGY', 'Manipulation of materials at the nano scale to create new applications in medicine, electronics, and materials.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('723a1b43-e2ad-45d9-9378-7d6777112880', 'SCIENTIFIC PROFESSIONAL SERVICES', 'Technical advisory in scientific and technical fields, such as engineering, environmental science, and data analysis.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('b5f54c40-a375-4f01-b722-f69ddad2d00f', 'GOVERNMENT AND PUBLIC ADMINISTRATION', 'Management of services and public policies for the organization and welfare of society.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('1502ec01-d445-43d4-b2ed-47b7fa9292ae', 'INTERNATIONAL ORGANIZATIONS', 'Entities that promote cooperation between countries in areas like health, trade, and human rights.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('72905033-597e-4f4b-9dcc-8e203c749358', 'SCIENTIFIC AND ACADEMIC RESEARCH', 'Studies and experiments to expand knowledge in sciences, humanities, and technology.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('8596e1ff-d541-4c34-80b1-f3cd35c1c30d', 'RENEWABLE ENERGY', 'Energy generation from sustainable sources, such as solar, wind, and hydroelectric.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('2706805c-2820-45bb-98cd-286f5c894267', 'SPACE INDUSTRY', 'Development and manufacturing of technology for space exploration and utilization.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('3cf34a22-23a4-4561-9e65-139a1ac4988e', 'CIRCULAR ECONOMY', 'Model that promotes reducing, reusing, and recycling materials to minimize waste.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('a5b4a20c-ec1c-402b-8da6-e49435e5b050', 'COMMERCIAL AEROSPACE', 'Development of aircraft and services for commercial air transport and space exploration.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('61e92caa-5e6d-40ef-9b0d-82669df77735', 'DEFENSE AND SECURITY', 'Manufacturing of equipment and technology for national and international security and protection.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('105b41d3-60ac-43fe-91f0-91627bd1e521', 'SPACE EXPLORATION', 'Research and development of technology for space exploration and future colonization.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('f4a0199d-dab0-4fa3-86d3-25a68050e45a', 'SHIPBUILDING', 'Design and manufacturing of commercial and military vessels, including ships and platforms.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('ec807245-9983-4367-bea3-62f17ad9da79', 'OFFSHORE INDUSTRY', 'Extraction and processing of natural resources at sea, such as oil and gas.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('b4201256-cf72-456f-9ea2-d25b215b5c1d', 'MARITIME TRANSPORT', 'Movement of goods across maritime routes, essential for international trade.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('669974f9-d7cc-469d-bcf2-8af68635ade3', 'AGROCHEMICALS', 'Manufacturing of chemical products to improve agricultural productivity, such as fertilizers and pesticides.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('7125488e-2fa5-49f6-a94f-67870cf00761', 'PROCESSED FOODS', 'Production of ready-to-consume foods through preservation and packaging processes.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('b582ace7-1bc7-47fd-90bc-fb2446efba96', 'BIOFUELS', 'Production of sustainable fuels from organic matter, such as ethanol and biodiesel', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('24db9223-c610-4759-b884-f9c5a79760f7', 'FILM AND AUDIOVISUAL PRODUCTION', 'Creation of entertainment and educational content in audiovisual formats.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('60e99b6c-744d-4d21-9aa8-0585dbcb4f9f', 'MUSIC AND RECORDINGS', 'Production and distribution of recorded music, concerts, and other music-related products.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('188e0db3-a6b5-46dd-b3b6-520a19266409', 'GAMES AND VIDEO GAMES', 'Development and commercialization of electronic and physical games for entertainment.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('0aa39864-1953-4a2c-bdda-45ec81bfc500', 'SPORTS AND ENTERTAINMENT', 'Organization of sporting events and live entertainment shows.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('488ad655-590b-4913-a494-ef7643f74bcf', 'FASHION AND ACCESSORIES', 'Design, production, and sale of clothing, jewelry, and fashion accessories.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('18a8211f-1fc1-425d-bfea-66fa68668795', 'JEWELRY AND WATCHES', 'Manufacturing of luxury items, such as jewelry and watches, for personal consumption and collecting.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('27888fab-3c1f-4685-8428-ebccb582681c', 'COSMETICS AND MAKEUP', 'Manufacturing of beauty products for personal and aesthetic care.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('3c4a90ce-5ce9-4949-b99f-36f19425fb34', 'PERSONAL CARE PRODUCTS', 'Production of hygiene and personal care items, like soaps, creams, and deodorants.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('6835dc6c-35dd-4709-97a6-38e19f63b189', 'PERFUMERY', 'Development of fragrances for personal use and ambiance, in both luxury and general consumption formats.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('cffe2114-c389-4175-af4f-1b23617951c9', 'WASTE MANAGEMENT', 'Collection, treatment, and recycling of waste to reduce environmental impact.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('4de92586-dbe4-4206-84bb-fd580b95e63b', 'POLLUTION CONTROL', 'Services and technologies to reduce pollution and improve environmental quality.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('0c57bc16-cad8-4494-99e2-3771b62c2ec4', 'FITNESS CENTERS AND GYMS', 'Facilities dedicated to physical activity and fitness.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('77374884-5d97-42dc-b442-00bf6f5a65c9', 'HEALTH SERVICES', 'Provision of medical and healthcare services for the health and wellness of the population.', true, NOW());
INSERT INTO t_industries (id, name, description, is_active, created_at) VALUES ('952bb19a-ac3f-4b13-a2be-7819e94efcea', 'REAL ESTATE DEVELOPMENT', 'Construction and promotion of residential and commercial properties.', true, NOW());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*DATA INICIAL DE PAISES*/
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('f6cbb6f4-2501-4fac-9798-1eb439be2084', 'DOM', 'DOMINICAN REPUBLIC', '18.735693', '-70.162651', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('fb62e568-6e6d-4906-b45a-ce19c091da2d', 'CHL', 'CHILE', '-35.675147', '-71.542969', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('08535f30-8b54-4094-9f36-4132a00dd64c', 'COL', 'COLOMBIA', '4.570868', '-74.297333', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('60b73634-295e-41f1-a4b3-a27468dbc7e1', 'BOL', 'BOLIVIA', '-16.290154', '-63.588653', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('3426dcbe-8f7e-4195-ba0a-c5604545ea99', 'CRI', 'COSTA RICA', '9.748917', '-83.753428', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('0273ea22-fc10-4e18-8b57-1f3e6770b6dc', 'DEU', 'GERMANY', '51.165691', '10.451526', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('785c37a8-7cab-47a6-9123-deac3bcb0fb0', 'PAN', 'PANAMA', '8.537981', '-80.782127', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('4c241895-b2d0-49c2-bf89-e18372927348', 'SLV', 'EL SALVADOR', '13.794185', '-88.89653', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('e3def8a3-6f71-49d5-9d24-c42dbcbed593', 'USA', 'UNITED STATES', '37.09024', '-95.712891', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('da4d0292-617e-4205-8bce-a9c808c4e3b9', 'PER', 'PERU', '-9.189967', '-75.015152', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('b76343f9-5cbb-4103-a9e0-4b4fe5890072', 'GTM', 'GUATEMALA', '15.783471', '-90.230759', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('9901ccb0-86bc-459e-82ad-909bf22747d8', 'ECU', 'ECUADOR', '-1.831239', '-78.183406', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('d3cf6e0f-10a5-4f35-bc7e-1bdcdd144a38', 'PRI', 'PUERTO RICO', '18.220833', '-66.590149', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('f2c251c9-93ce-4c59-a167-854270a7953d', 'VEN', 'VENEZUELA', '6.42375', '-66.58973', NOW());
INSERT INTO t_countries (id, name_short, name, latitude, longitude, created_at) VALUES ('afb3cb76-9dc6-42a5-9a86-3d9207aaef3b', 'NIC', 'NICARAGUA', '12.865416', '-85.207229', NOW());
/*--------------------------------------------------------------------------------------------------------------------------------*/

/*DATA INICIAL DE ESTADOS*/
INSERT INTO t_states (id, name_short, name, latitude, longitude, country_id, created_at) VALUES ('2cc81a25-e4da-40f4-a6e6-3ef10bf0741f', 'PR', 'PUERTO RICO', '18.220833', '-66.590149', 'd3cf6e0f-10a5-4f35-bc7e-1bdcdd144a38', NOW());
/*--------------------------------------------------------------------------------------------------------------------------------*/