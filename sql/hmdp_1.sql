USE hmdp_1;

DROP TABLE IF EXISTS `tb_blog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_blog` (
                           `id` bigint unsigned NOT NULL COMMENT 'primary key',
                           `shop_id` bigint NOT NULL COMMENT 'merchant ID',
                           `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                           `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'title',
                           `images` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'shop-exploration photos, up to 9 images separated by commas',
                           `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'shop-exploration text description',
                           `liked` int unsigned DEFAULT '0' COMMENT 'like count',
                           `comments` int unsigned DEFAULT NULL COMMENT 'comment count',
                           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                           `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_blog`
--

LOCK TABLES `tb_blog` WRITE;
/*!40000 ALTER TABLE `tb_blog` DISABLE KEYS */;
INSERT INTO `tb_blog` VALUES
(4,4,1987042234935279617,'An endless romantic night | sipping red wine among the flowers with a tomahawk steak 🍷🥩','/imgs/blogs/7/14/4771fefb-1a87-4252-816c-9f7ec41ffa4a.jpg,/imgs/blogs/4/10/2f07e3c9-ddce-482d-9ea7-c21450f8d7cd.jpg,/imgs/blogs/2/6/b0756279-65da-4f2d-b62a-33f74b06454a.jpg,/imgs/blogs/10/7/7e97f47d-eb49-4dc9-a583-95faa7aed287.jpg,/imgs/blogs/1/2/4a7b496b-2a08-4af7-aa95-df2c3bd0ef97.jpg,/imgs/blogs/14/3/52b290eb-8b5d-403b-8373-ba0bb856d18e.jpg','Life is half fireworks, half poetry.<br/>When everyday life feels dull, make your own little ceremony 🍒<br/><br/>🏰「Xiaozhuli · Secret Romantic Garden Restaurant」🏰<br/><br/>A dreamy garden-style western restaurant filled with flowers, candlelight, and wine. The whole space feels soft, romantic, and perfect for photos.<br/><br/>📍Address: No. 200 Yan''an Road (across from Carrefour)<br/>🚌Metro: Exit B, Ding''an Road Station on Line 1. Turn right through the passage and you will see it.<br/><br/>Recommended dishes:<br/>「Tomahawk Steak」Juicy, tender, and full of smoky aroma.<br/>「Creamy Bacon Pasta」Rich, creamy, and seriously comforting.<br/>「Sea Bass with Cilantro Sauce」Tender fish with a bold spicy kick.<br/><br/>Service was attentive, and the staff were happy to help with recommendations and photos.<br/>If you want flowers, wine, and a relaxed date-night mood in one place, this one is worth visiting 🌸',1,104,'2021-12-28 11:50:01','2025-11-08 06:28:33'),
(5,1,1987042234935279617,'Around ¥30 per person — I am obsessed with this Hong Kong-style cafe in Hangzhou‼️','/imgs/blogs/4/7/863cc302-d150-420d-a596-b16e9232a1a6.jpg,/imgs/blogs/11/12/8b37d208-9414-4e78-b065-9199647bb3e3.jpg,/imgs/blogs/4/1/fa74a6d6-3026-4cb7-b0b6-35abb1e52d11.jpg,/imgs/blogs/9/12/ac2ce2fb-0605-4f14-82cc-c962b8c86688.jpg,/imgs/blogs/4/0/26a7cd7e-6320-432c-a0b4-1b7418f45ec7.jpg,/imgs/blogs/15/9/cea51d9b-ac15-49f6-b9f1-9cf81e9b9c85.jpg','Another great Hong Kong-style cafe in Hangzhou 🍴 Great for photos, easy on the wallet, and packed with nostalgic TVB vibes 📺📷<br/><br/>Shop: Jiuji Ice House (Ledi Harbor)<br/>Address: B1, Ledi Harbor, Lishui Road, Hangzhou (next to the skating rink)<br/><br/>Best picks:<br/>✔️Soul-Satisfying BBQ Rice (¥38) Sweet char siu, jammy eggs, and rich sauce over rice.<br/>✔️Causeway Bay Lava French Toast (¥28) Crispy outside with a flowing creamy filling.<br/>✔️Heavenly Hong Kong French Toast (¥16) Buttery, soft inside, and finished with condensed milk.<br/>✔️Nostalgic Sweet-and-Sour Fried Egg Rice (¥28) Fluffy eggs, chicken cutlet, and sweet-sour sauce.<br/>✔️BBQ Combo Platter (¥66) Roast goose and char siu with authentic Cantonese flavor.<br/>✔️Braised Crispy Pigeon (¥18.8) Crisp skin and satisfying texture.<br/>✔️Hong Kong Bear Silk Stocking Milk Tea (¥19) Cute, photogenic, and fragrant.<br/><br/>If you want a budget-friendly Hong Kong cafe with fun visuals and comforting food, this place is a solid choice.',2,0,'2021-12-28 12:57:49','2025-11-08 06:28:33'),
(6,10,1987041610793484289,'Hangzhou weekend idea | Horseback riding for just ¥50 🐎','/imgs/blogs/blog1.jpg','Hangzhou weekend idea | Horseback riding for just ¥50 🐎',1,0,'2022-01-11 08:05:47','2025-11-08 06:28:37'),
(7,10,1987041610793484289,'Hangzhou weekend idea | Horseback riding for just ¥50 🐎','/imgs/blogs/blog1.jpg','Hangzhou weekend idea | Horseback riding for just ¥50 🐎',1,0,'2022-01-11 08:05:47','2025-11-08 06:28:37');
/*!40000 ALTER TABLE `tb_blog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_blog_comments`
--

DROP TABLE IF EXISTS `tb_blog_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_blog_comments` (
                                    `id` bigint unsigned NOT NULL COMMENT 'primary key',
                                    `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                                    `blog_id` bigint unsigned NOT NULL COMMENT 'shop-exploration ID',
                                    `parent_id` bigint unsigned NOT NULL COMMENT 'associated first-level comment ID; 0 if this is a top-level comment',
                                    `answer_id` bigint unsigned NOT NULL COMMENT 'replied comment ID',
                                    `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'reply content',
                                    `liked` int unsigned DEFAULT NULL COMMENT 'like count',
                                    `status` tinyint unsigned DEFAULT NULL COMMENT 'status: 0 normal, 1 reported, 2 hidden',
                                    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_blog_comments`
--

LOCK TABLES `tb_blog_comments` WRITE;
/*!40000 ALTER TABLE `tb_blog_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_blog_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_follow`
--

DROP TABLE IF EXISTS `tb_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_follow` (
                             `id` bigint NOT NULL COMMENT 'primary key',
                             `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                             `follow_user_id` bigint unsigned NOT NULL COMMENT 'associated user ID',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_follow`
--

LOCK TABLES `tb_follow` WRITE;
/*!40000 ALTER TABLE `tb_follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_rollback_failure_log`
--

DROP TABLE IF EXISTS `tb_rollback_failure_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_rollback_failure_log` (
                                           `id` bigint NOT NULL COMMENT 'primary key',
                                           `voucher_id` bigint unsigned NOT NULL COMMENT 'voucher ID',
                                           `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                                           `order_id` bigint DEFAULT NULL COMMENT 'order ID',
                                           `trace_id` bigint DEFAULT NULL COMMENT 'trace identifier',
                                           `detail` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'failure details',
                                           `result_code` int DEFAULT NULL COMMENT 'Lua return code (BaseCode)',
                                           `retry_attempts` int DEFAULT NULL COMMENT 'retry attempts',
                                           `source` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'source component',
                                           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                           `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           KEY `idx_voucher_user` (`voucher_id`,`user_id`) USING BTREE,
                                           KEY `idx_trace_id` (`trace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT COMMENT='Redis rollback failure log table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_rollback_failure_log`
--

LOCK TABLES `tb_rollback_failure_log` WRITE;
/*!40000 ALTER TABLE `tb_rollback_failure_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_rollback_failure_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_seckill_voucher_0`
--

DROP TABLE IF EXISTS `tb_seckill_voucher_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_seckill_voucher_0` (
                                        `id` bigint NOT NULL,
                                        `voucher_id` bigint unsigned NOT NULL COMMENT 'associated voucher ID',
                                        `init_stock` int NOT NULL COMMENT 'Initialize stock',
                                        `stock` int NOT NULL COMMENT 'stock',
                                        `allowed_levels` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'allowed membership levels, comma-separated, for example: "1,2,3"',
                                        `min_level` int DEFAULT NULL COMMENT 'minimum membership level',
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                        `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'effective time',
                                        `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'expiration time',
                                        `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT COMMENT='seckill voucher table with a one-to-one relationship to vouchers';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_seckill_voucher_0`
--

LOCK TABLES `tb_seckill_voucher_0` WRITE;
/*!40000 ALTER TABLE `tb_seckill_voucher_0` DISABLE KEYS */;
INSERT INTO `tb_seckill_voucher_0` VALUES (1987043235650076673,1,200,200,'1,2',1,'2025-11-08 06:23:19','2025-11-02 13:00:00','2025-12-02 15:59:59','2025-11-20 07:23:03');
/*!40000 ALTER TABLE `tb_seckill_voucher_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_seckill_voucher_1`
--

DROP TABLE IF EXISTS `tb_seckill_voucher_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_seckill_voucher_1` (
                                        `id` bigint NOT NULL,
                                        `voucher_id` bigint unsigned NOT NULL COMMENT 'associated voucher ID',
                                        `init_stock` int NOT NULL COMMENT 'Initialize stock',
                                        `stock` int NOT NULL COMMENT 'stock',
                                        `allowed_levels` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'allowed membership levels, comma-separated, for example: "1,2,3"',
                                        `min_level` int DEFAULT NULL COMMENT 'minimum membership level',
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                        `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'effective time',
                                        `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'expiration time',
                                        `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT COMMENT='seckill voucher table with a one-to-one relationship to vouchers';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_seckill_voucher_1`
--

LOCK TABLES `tb_seckill_voucher_1` WRITE;
/*!40000 ALTER TABLE `tb_seckill_voucher_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_seckill_voucher_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_shop`
--

DROP TABLE IF EXISTS `tb_shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_shop` (
                           `id` bigint unsigned NOT NULL COMMENT 'primary key',
                           `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'shop name',
                           `type_id` bigint unsigned NOT NULL COMMENT 'shop type ID',
                           `images` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Shop pictures, multiple pictures separated by commas',
                           `area` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'business district, for example Lujiazui',
                           `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'address',
                           `x` double unsigned NOT NULL COMMENT 'longitude',
                           `y` double unsigned NOT NULL COMMENT 'latitude',
                           `avg_price` bigint unsigned DEFAULT NULL COMMENT 'average price as an integer',
                           `sold` int(10) unsigned zerofill NOT NULL COMMENT 'sales volume',
                           `comments` int(10) unsigned zerofill NOT NULL COMMENT 'comment count',
                           `score` int(2) unsigned zerofill NOT NULL COMMENT 'rating, stored as score x10 to avoid decimals',
                           `open_hours` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'business hours, for example 10:00-22:00',
                           `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                           `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `foreign_key_type` (`type_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_shop`
--

LOCK TABLES `tb_shop` WRITE;
/*!40000 ALTER TABLE `tb_shop` DISABLE KEYS */;
INSERT INTO `tb_shop` VALUES (1,'103 Tea Cafe',1,'https://qcloud.dpfile.com/pc/jiclIsCKmOI2arxKN1Uf0Hx3PucIJH8q0QSz-Z8llzcN56-_QiKuOvyio1OOxsRtFoXqu0G3iT2T27qat3WhLVEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vfCF2ubeXzk49OsGrXt_KYDCngOyCwZK-s3fqawWswzk.jpg,https://qcloud.dpfile.com/pc/IOf6VX3qaBgFXFVgp75w-KKJmWZjFc8GXDU8g9bQC6YGCpAmG00QbfT4vCCBj7njuzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg','Daguan','No. 29, Jinchang Wenyuan, Jinhua Road',120.149192,30.316078,80,0000004215,0000003035,37,'10:00-22:00','2021-12-22 10:10:39','2022-01-13 09:32:19'),(2,'Cai Ma Hongtao BBQ · Old Beijing Copper Pot Lamb Hotpot',1,'https://p0.meituan.net/bbia/c1870d570e73accbc9fee90b48faca41195272.jpg,http://p0.meituan.net/mogu/397e40c28fc87715b3d5435710a9f88d706914.jpg,https://qcloud.dpfile.com/pc/MZTdRDqCZdbPDUO0Hk6lZENRKzpKRF7kavrkEI99OxqBZTzPfIxa5E33gBfGouhFuzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg','Gongchen Bridge / Shangtang','No. 1035 Shangtang Road (next to ICBC)',120.151505,30.333422,85,0000002160,0000001460,46,'11:30-03:00','2021-12-22 11:00:13','2022-01-11 08:12:26'),(3,'Xin Bailu Restaurant (Canal Shangjie)',1,'https://p0.meituan.net/biztone/694233_1619500156517.jpeg,https://img.meituan.net/msmerchant/876ca8983f7395556eda9ceb064e6bc51840883.png,https://img.meituan.net/msmerchant/86a76ed53c28eff709a36099aefe28b51554088.png','Canal Shangjie','F5, Canal Shangjie Shopping Center, No. 2 Taizhou Road',120.151954,30.32497,61,0000012035,0000008045,47,'10:30-21:00','2021-12-22 11:10:05','2022-01-11 08:12:42'),(4,'Mamala (Hangzhou Ledi Harbor)',1,'https://img.meituan.net/msmerchant/232f8fdf09050838bd33fb24e79f30f9606056.jpg,https://qcloud.dpfile.com/pc/rDe48Xe15nQOHCcEEkmKUp5wEKWbimt-HDeqYRWsYJseXNncvMiXbuED7x1tXqN4uzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg','Gongchen Bridge / Shangtang','B115, Level 1, Phase 2, Ledi Harbor Mall, No. 66 Lishui Road',120.146659,30.312742,290,0000013519,0000009529,49,'11:00-22:00','2021-12-22 11:17:15','2022-01-11 08:12:51'),(5,'Haidilao Hot Pot (Crystal City Mall)',1,'https://img.meituan.net/msmerchant/054b5de0ba0b50c18a620cc37482129a45739.jpg,https://img.meituan.net/msmerchant/59b7eff9b60908d52bd4aea9ff356e6d145920.jpg,https://qcloud.dpfile.com/pc/Qe2PTEuvtJ5skpUXKKoW9OQ20qc7nIpHYEqJGBStJx0mpoyeBPQOJE4vOdYZwm9AuzFvxlbkWx5uwqY2qcjixFEuLYk00OmSS1IdNpm8K8sG4JN9RIm2mTKcbLtc2o2vmIU_8ZGOT1OjpJmLxG6urQ.jpg','Daguan','F6, Crystal City Shopping Center, No. 458 Shangtang Road',120.15778,30.310633,104,0000004125,0000002764,49,'10:00-07:00','2021-12-22 11:20:58','2022-01-11 08:13:01'),(6,'Xingfuli Old Beijing Hotpot (Silian)',1,'https://img.meituan.net/msmerchant/e71a2d0d693b3033c15522c43e03f09198239.jpg,https://img.meituan.net/msmerchant/9f8a966d60ffba00daf35458522273ca658239.jpg,https://img.meituan.net/msmerchant/ef9ca5ef6c05d381946fe4a9aa7d9808554502.jpg','Gongchen Bridge / Shangtang','No. 166 Silian, No. 189 Jinhua South Road',120.148603,30.318618,130,0000009531,0000007324,46,'11:00-13:50,17:00-20:50','2021-12-22 11:24:53','2022-01-11 08:13:09'),(7,'Luyu Grilled Fish (Gongshu Wanda Plaza)',1,'https://img.meituan.net/msmerchant/909434939a49b36f340523232924402166854.jpg,https://img.meituan.net/msmerchant/32fd2425f12e27db0160e837461c10303700032.jpg,https://img.meituan.net/msmerchant/f7022258ccb8dabef62a0514d3129562871160.jpg','North New Town','Room 409, Unit 2, Building 4, Wanda Commercial Center, No. 666 Hangxing Road (Shop 4005)',120.124691,30.336819,85,0000002631,0000001320,47,'00:00-24:00','2021-12-22 11:40:52','2022-01-11 08:13:19'),(8,'Asakusa House Sushi (Canal Shangjie)',1,'https://img.meituan.net/msmerchant/cf3dff697bf7f6e11f4b79c4e7d989e4591290.jpg,https://img.meituan.net/msmerchant/0b463f545355c8d8f021eb2987dcd0c8567811.jpg,https://img.meituan.net/msmerchant/c3c2516939efaf36c4ccc64b0e629fad587907.jpg','Canal Shangjie','B1, Canal Shangjie, No. 80 Jinhua Road, Gongshu District',120.150526,30.325231,88,0000002406,0000001206,46,' 11:00-21:30','2021-12-22 11:51:06','2022-01-11 08:13:25'),(9,'Yang Lao San Lamb Spine & Short Rib Charcoal Hotpot (Canal Shangjie)',1,'https://p0.meituan.net/biztone/163160492_1624251899456.jpeg,https://img.meituan.net/msmerchant/e478eb16f7e31a7f8b29b5e3bab6de205500837.jpg,https://img.meituan.net/msmerchant/6173eb1d18b9d70ace7fdb3f2dd939662884857.jpg','Canal Shangjie','F5, Canal Shangjie Shopping Center, No. 2 Taizhou Road',120.150598,30.325251,101,0000002763,0000001363,44,'11:00-21:30','2021-12-22 11:53:59','2022-01-11 08:13:34'),(10,'Kelaidi KTV (Canal Shangjie)',2,'https://p0.meituan.net/joymerchant/a575fd4adb0b9099c5c410058148b307-674435191.jpg,https://p0.meituan.net/merchantpic/68f11bf850e25e437c5f67decfd694ab2541634.jpg,https://p0.meituan.net/dpdeal/cb3a12225860ba2875e4ea26c6d14fcc197016.jpg','Canal Shangjie','F4, Canal Shangjie Shopping Center, No. 2 Taizhou Road',120.149093,30.324666,67,0000026891,0000000902,37,'00:00-24:00','2021-12-22 12:25:16','2021-12-22 12:25:16'),(11,'INLOVE KTV (Crystal City)',2,'https://p0.meituan.net/dpmerchantpic/53e74b200211d68988a4f02ae9912c6c1076826.jpg,https://qcloud.dpfile.com/pc/4iWtIvzLzwM2MGgyPu1PCDb4SWEaKqUeHm--YAt1EwR5tn8kypBcqNwHnjg96EvT_Gd2X_f-v9T8Yj4uLt25Gg.jpg,https://qcloud.dpfile.com/pc/WZsJWRI447x1VG2x48Ujgu7vwqksi_9WitdKI4j3jvIgX4MZOpGNaFtM93oSSizbGybIjx5eX6WNgCPvcASYAw.jpg','Crystal City','Level 6, Crystal City Shopping Center, No. 458 Shangtang Road',120.15853,30.310002,75,0000035977,0000005684,47,'11:30-06:00','2021-12-22 12:29:02','2021-12-22 12:39:00'),(12,'Mei (Hangzhou Ledi Harbor)',2,'https://p0.meituan.net/dpmerchantpic/63833f6ba0393e2e8722420ef33f3d40466664.jpg,https://p0.meituan.net/dpmerchantpic/ae3c94cc92c529c4b1d7f68cebed33fa105810.png,','Ledi Harbor','F4, Ledi Harbor, No. 58 Lishui Road',120.14983,30.31211,88,0000006444,0000000235,46,'10:00-02:00','2021-12-22 12:34:34','2021-12-22 12:34:34'),(13,'Ou K La Party KTV (Beicheng Tiandi)',2,'https://p1.meituan.net/merchantpic/598c83a8c0d06fe79ca01056e214d345875600.jpg,https://qcloud.dpfile.com/pc/HhvI0YyocYHRfGwJWqPQr34hRGRl4cWdvlNwn3dqghvi4WXlM2FY1te0-7pE3Wb9_Gd2X_f-v9T8Yj4uLt25Gg.jpg,https://qcloud.dpfile.com/pc/F5ZVzZaXFE27kvQzPnaL4V8O9QCpVw2nkzGrxZE8BqXgkfyTpNExfNG5CEPQX4pjGybIjx5eX6WNgCPvcASYAw.jpg','D32 Tianyang Shopping Center','Level 5, Beicheng Tiandi, No. 567 Huzhou Street',120.130453,30.327655,58,0000018997,0000001857,41,'12:00-02:00','2021-12-22 12:38:54','2021-12-22 12:40:04'),(14,'Star Gathering KTV (Gongshu Wanda)',2,'https://p0.meituan.net/dpmerchantpic/f4cd6d8d4eb1959c3ea826aa05a552c01840451.jpg,https://p0.meituan.net/dpmerchantpic/2efc07aed856a8ab0fc75c86f4b9b0061655777.jpg,https://qcloud.dpfile.com/pc/zWfzzIorCohKT0bFwsfAlHuayWjI6DBEMPHHncmz36EEMU9f48PuD9VxLLDAjdoU_Gd2X_f-v9T8Yj4uLt25Gg.jpg','North New Town','Levels 1-2, Tower C, Wanda Plaza, No. 666 Hangxing Road',120.128958,30.337252,60,0000017771,0000000685,47,'10:00-22:00','2021-12-22 12:48:54','2021-12-22 12:48:54');
/*!40000 ALTER TABLE `tb_shop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_shop_type`
--

DROP TABLE IF EXISTS `tb_shop_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_shop_type` (
                                `id` bigint unsigned NOT NULL COMMENT 'primary key',
                                `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'type name',
                                `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'icon',
                                `sort` int unsigned DEFAULT NULL COMMENT 'sort order',
                                `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_shop_type`
--

LOCK TABLES `tb_shop_type` WRITE;
/*!40000 ALTER TABLE `tb_shop_type` DISABLE KEYS */;
INSERT INTO `tb_shop_type` VALUES (1,'Food','/types/ms.png',1,'2021-12-22 12:17:47','2021-12-23 03:24:31'),(2,'KTV','/types/KTV.png',2,'2021-12-22 12:18:27','2021-12-23 03:24:31'),(3,'Beauty & Hair','/types/lrmf.png',3,'2021-12-22 12:18:48','2021-12-23 03:24:31'),(4,'Fitness','/types/jsyd.png',10,'2021-12-22 12:19:04','2021-12-23 03:24:31'),(5,'Massage & Foot Spa','/types/amzl.png',5,'2021-12-22 12:19:27','2021-12-23 03:24:31'),(6,'Beauty Spa','/types/spa.png',6,'2021-12-22 12:19:35','2021-12-23 03:24:31'),(7,'Family Fun','/types/qzyl.png',7,'2021-12-22 12:19:53','2021-12-23 03:24:31'),(8,'Bars','/types/jiuba.png',8,'2021-12-22 12:20:02','2021-12-23 03:24:31'),(9,'Party House','/types/hpg.png',9,'2021-12-22 12:20:08','2021-12-23 03:24:31'),(10,'Lashes & Nails','/types/mjmj.png',4,'2021-12-22 12:21:46','2021-12-23 03:24:31');
/*!40000 ALTER TABLE `tb_shop_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_sign`
--

DROP TABLE IF EXISTS `tb_sign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_sign` (
                           `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
                           `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                           `year` year NOT NULL COMMENT 'sign-in year',
                           `month` tinyint NOT NULL COMMENT 'sign-in month',
                           `date` date NOT NULL COMMENT 'sign-in date',
                           `is_backup` tinyint unsigned DEFAULT NULL COMMENT 'whether it is a makeup sign-in',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_sign`
--

LOCK TABLES `tb_sign` WRITE;
/*!40000 ALTER TABLE `tb_sign` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_sign` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user_0`
--

DROP TABLE IF EXISTS `tb_user_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_0` (
                             `id` bigint unsigned NOT NULL COMMENT 'primary key',
                             `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'mobile number',
                             `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'password, stored in encrypted form',
                             `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'Nickname, default is user ID',
                             `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'avatar',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uniqe_key_phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_0`
--

LOCK TABLES `tb_user_0` WRITE;
/*!40000 ALTER TABLE `tb_user_0` DISABLE KEYS */;
INSERT INTO `tb_user_0` VALUES (1987041610793484289,'13686869696','','Little Fish','/imgs/blogs/blog1.jpg','2025-11-08 06:16:52','2025-11-08 06:17:40'),(1987042234935279617,'13838411438','','Coco Goes Meatless Today','/imgs/icons/kkjtbcr.jpg','2025-11-08 06:19:20','2025-11-08 06:19:55'),(1987042505555968001,'13456789001','','Cutie Pie','/imgs/icons/user5-icon.png','2025-11-08 06:20:25','2025-11-08 06:20:47');
/*!40000 ALTER TABLE `tb_user_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user_1`
--

DROP TABLE IF EXISTS `tb_user_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_1` (
                             `id` bigint unsigned NOT NULL COMMENT 'primary key',
                             `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'mobile number',
                             `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'password, stored in encrypted form',
                             `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'Nickname, default is user ID',
                             `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'avatar',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE KEY `uniqe_key_phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_1`
--

LOCK TABLES `tb_user_1` WRITE;
/*!40000 ALTER TABLE `tb_user_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user_info_0`
--

DROP TABLE IF EXISTS `tb_user_info_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_info_0` (
                                  `id` bigint unsigned NOT NULL COMMENT 'primary key',
                                  `user_id` bigint unsigned NOT NULL COMMENT 'primary key，user ID',
                                  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'city name',
                                  `introduce` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'profile introduction, up to 128 characters',
                                  `fans` int unsigned DEFAULT '0' COMMENT 'fan count',
                                  `followee` int unsigned DEFAULT '0' COMMENT 'following count',
                                  `gender` tinyint unsigned DEFAULT '0' COMMENT 'Gender, 0: male, 1: female',
                                  `birthday` date DEFAULT NULL COMMENT 'birthday',
                                  `credits` int unsigned DEFAULT '0' COMMENT 'points',
                                  `level` tinyint unsigned DEFAULT '0' COMMENT 'membership level, 0-9; 0 means not activated',
                                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_info_0`
--

LOCK TABLES `tb_user_info_0` WRITE;
/*!40000 ALTER TABLE `tb_user_info_0` DISABLE KEYS */;
INSERT INTO `tb_user_info_0` VALUES (1987041610868981762,1987041610793484289,'',NULL,0,0,0,NULL,0,1,'2025-11-08 06:16:52','2025-11-08 06:16:52'),(1987042234943668226,1987042234935279617,'',NULL,0,0,0,NULL,0,1,'2025-11-08 06:19:20','2025-11-08 06:19:20'),(1987042505560162305,1987042505555968001,'',NULL,0,0,0,NULL,0,1,'2025-11-08 06:20:25','2025-11-08 06:20:25');
/*!40000 ALTER TABLE `tb_user_info_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user_info_1`
--

DROP TABLE IF EXISTS `tb_user_info_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_info_1` (
                                  `id` bigint unsigned NOT NULL COMMENT 'primary key',
                                  `user_id` bigint unsigned NOT NULL COMMENT 'primary key，user ID',
                                  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'city name',
                                  `introduce` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'profile introduction, up to 128 characters',
                                  `fans` int unsigned DEFAULT '0' COMMENT 'fan count',
                                  `followee` int unsigned DEFAULT '0' COMMENT 'following count',
                                  `gender` tinyint unsigned DEFAULT '0' COMMENT 'Gender, 0: male, 1: female',
                                  `birthday` date DEFAULT NULL COMMENT 'birthday',
                                  `credits` int unsigned DEFAULT '0' COMMENT 'points',
                                  `level` tinyint unsigned DEFAULT '0' COMMENT 'membership level, 0-9; 0 means not activated',
                                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_info_1`
--

LOCK TABLES `tb_user_info_1` WRITE;
/*!40000 ALTER TABLE `tb_user_info_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user_info_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user_phone_0`
--

DROP TABLE IF EXISTS `tb_user_phone_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_phone_0` (
                                   `id` bigint NOT NULL COMMENT 'primary key ID',
                                   `user_id` bigint NOT NULL COMMENT 'user ID',
                                   `phone` varchar(512) NOT NULL COMMENT 'mobile number',
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                   PRIMARY KEY (`id`),
                                   KEY `phone_idx` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User mobile phone table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_phone_0`
--

LOCK TABLES `tb_user_phone_0` WRITE;
/*!40000 ALTER TABLE `tb_user_phone_0` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user_phone_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user_phone_1`
--

DROP TABLE IF EXISTS `tb_user_phone_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user_phone_1` (
                                   `id` bigint NOT NULL COMMENT 'primary key ID',
                                   `user_id` bigint NOT NULL COMMENT 'user ID',
                                   `phone` varchar(512) NOT NULL COMMENT 'mobile number',
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                   PRIMARY KEY (`id`),
                                   KEY `phone_idx` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User mobile phone table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user_phone_1`
--

LOCK TABLES `tb_user_phone_1` WRITE;
/*!40000 ALTER TABLE `tb_user_phone_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_user_phone_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_0`
--

DROP TABLE IF EXISTS `tb_voucher_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_0` (
                                `id` bigint unsigned NOT NULL COMMENT 'primary key',
                                `shop_id` bigint unsigned DEFAULT NULL COMMENT 'shop ID',
                                `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Voucher title',
                                `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'subtitle',
                                `rules` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'usage rules',
                                `pay_value` bigint unsigned NOT NULL COMMENT 'payment amount in cents, for example 200 means 2 yuan',
                                `actual_value` bigint NOT NULL COMMENT 'discount amount in cents, for example 200 means 2 yuan',
                                `type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0 normal voucher; 1 seckill voucher',
                                `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '1 listed; 2 unlisted; 3 expired',
                                `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_0`
--

LOCK TABLES `tb_voucher_0` WRITE;
/*!40000 ALTER TABLE `tb_voucher_0` DISABLE KEYS */;
INSERT INTO `tb_voucher_0` VALUES (1,1,'¥80 Cash Voucher','Valid Monday to Sunday','No special rules',20,100,1,1,'2025-11-08 06:23:19','2025-11-20 07:23:03');
/*!40000 ALTER TABLE `tb_voucher_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_1`
--

DROP TABLE IF EXISTS `tb_voucher_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_1` (
                                `id` bigint unsigned NOT NULL COMMENT 'primary key',
                                `shop_id` bigint unsigned DEFAULT NULL COMMENT 'shop ID',
                                `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Voucher title',
                                `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'subtitle',
                                `rules` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'usage rules',
                                `pay_value` bigint unsigned NOT NULL COMMENT 'payment amount in cents, for example 200 means 2 yuan',
                                `actual_value` bigint NOT NULL COMMENT 'discount amount in cents, for example 200 means 2 yuan',
                                `type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0 normal voucher; 1 seckill voucher',
                                `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '1 listed; 2 unlisted; 3 expired',
                                `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_1`
--

LOCK TABLES `tb_voucher_1` WRITE;
/*!40000 ALTER TABLE `tb_voucher_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_order_0`
--

DROP TABLE IF EXISTS `tb_voucher_order_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_order_0` (
                                      `id` bigint NOT NULL COMMENT 'primary key',
                                      `user_id` bigint unsigned NOT NULL COMMENT 'User ID for placing the order',
                                      `voucher_id` bigint unsigned NOT NULL COMMENT 'Purchased voucher ID',
                                      `pay_type` tinyint unsigned NOT NULL DEFAULT '1' COMMENT 'payment method: 1 balance, 2 Alipay, 3 WeChat',
                                      `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT 'Order status: 1 normal, 2 canceled',
                                      `reconciliation_status` tinyint NOT NULL DEFAULT '1' COMMENT 'Reconciliation status: 1 pending; 2 abnormal; 3 inconsistent; 4 consistent',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'order time',
                                      `pay_time` timestamp NULL DEFAULT NULL COMMENT 'payment time',
                                      `use_time` timestamp NULL DEFAULT NULL COMMENT 'redemption time',
                                      `refund_time` timestamp NULL DEFAULT NULL COMMENT 'refund time',
                                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_order_0`
--

LOCK TABLES `tb_voucher_order_0` WRITE;
/*!40000 ALTER TABLE `tb_voucher_order_0` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_order_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_order_1`
--

DROP TABLE IF EXISTS `tb_voucher_order_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_order_1` (
                                      `id` bigint NOT NULL COMMENT 'primary key',
                                      `user_id` bigint unsigned NOT NULL COMMENT 'User ID for placing the order',
                                      `voucher_id` bigint unsigned NOT NULL COMMENT 'Purchased voucher ID',
                                      `pay_type` tinyint unsigned NOT NULL DEFAULT '1' COMMENT 'payment method: 1 balance, 2 Alipay, 3 WeChat',
                                      `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT 'Order status: 1 normal, 2 canceled',
                                      `reconciliation_status` tinyint NOT NULL DEFAULT '1' COMMENT 'Reconciliation status: 1 pending; 2 abnormal; 3 inconsistent; 4 consistent',
                                      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'order time',
                                      `pay_time` timestamp NULL DEFAULT NULL COMMENT 'payment time',
                                      `use_time` timestamp NULL DEFAULT NULL COMMENT 'redemption time',
                                      `refund_time` timestamp NULL DEFAULT NULL COMMENT 'refund time',
                                      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_order_1`
--

LOCK TABLES `tb_voucher_order_1` WRITE;
/*!40000 ALTER TABLE `tb_voucher_order_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_order_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_order_router_0`
--

DROP TABLE IF EXISTS `tb_voucher_order_router_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_order_router_0` (
                                             `id` bigint NOT NULL COMMENT 'primary key',
                                             `order_id` bigint NOT NULL COMMENT 'order ID',
                                             `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                                             `voucher_id` bigint unsigned NOT NULL COMMENT 'voucher ID',
                                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_order_router_0`
--

LOCK TABLES `tb_voucher_order_router_0` WRITE;
/*!40000 ALTER TABLE `tb_voucher_order_router_0` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_order_router_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_order_router_1`
--

DROP TABLE IF EXISTS `tb_voucher_order_router_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_order_router_1` (
                                             `id` bigint NOT NULL COMMENT 'primary key',
                                             `order_id` bigint NOT NULL COMMENT 'order ID',
                                             `user_id` bigint unsigned NOT NULL COMMENT 'user ID',
                                             `voucher_id` bigint unsigned NOT NULL COMMENT 'voucher ID',
                                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                             `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_order_router_1`
--

LOCK TABLES `tb_voucher_order_router_1` WRITE;
/*!40000 ALTER TABLE `tb_voucher_order_router_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_order_router_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_reconcile_log_0`
--

DROP TABLE IF EXISTS `tb_voucher_reconcile_log_0`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_reconcile_log_0` (
                                              `id` bigint NOT NULL COMMENT 'primary key',
                                              `order_id` bigint NOT NULL COMMENT 'order ID',
                                              `user_id` bigint unsigned NOT NULL COMMENT 'User ID for placing the order',
                                              `voucher_id` bigint unsigned NOT NULL COMMENT 'Purchased voucher ID',
                                              `message_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Kafka message UUID',
                                              `detail` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'difference details',
                                              `before_qty` int DEFAULT NULL COMMENT 'Stock quantity before changing',
                                              `change_qty` int DEFAULT NULL COMMENT 'change quantity',
                                              `after_qty` int DEFAULT NULL COMMENT 'Stock quantity after change',
                                              `trace_id` bigint DEFAULT NULL COMMENT 'trace identifier',
                                              `log_type` int DEFAULT '-1' COMMENT 'log type: -1 deduct, 1 restore',
                                              `business_type` int DEFAULT '1' COMMENT 'business type: 1 order created successfully, 2 order creation timed out, 3 order creation failed',
                                              `reconciliation_status` int NOT NULL DEFAULT '1' COMMENT 'Reconciliation status: 1 pending; 2 abnormal; 3 inconsistent; 4 consistent',
                                              `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                              `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_order_id` (`order_id`) USING BTREE,
                                              KEY `idx_message_id` (`message_id`) USING BTREE,
                                              KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_reconcile_log_0`
--

LOCK TABLES `tb_voucher_reconcile_log_0` WRITE;
/*!40000 ALTER TABLE `tb_voucher_reconcile_log_0` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_reconcile_log_0` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_voucher_reconcile_log_1`
--

DROP TABLE IF EXISTS `tb_voucher_reconcile_log_1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_voucher_reconcile_log_1` (
                                              `id` bigint NOT NULL COMMENT 'primary key',
                                              `order_id` bigint NOT NULL COMMENT 'order ID',
                                              `user_id` bigint unsigned NOT NULL COMMENT 'User ID for placing the order',
                                              `voucher_id` bigint unsigned NOT NULL COMMENT 'Purchased voucher ID',
                                              `message_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Kafka message UUID',
                                              `detail` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'difference details',
                                              `before_qty` int DEFAULT NULL COMMENT 'Stock quantity before changing',
                                              `change_qty` int DEFAULT NULL COMMENT 'change quantity',
                                              `after_qty` int DEFAULT NULL COMMENT 'Stock quantity after change',
                                              `trace_id` bigint DEFAULT NULL COMMENT 'trace identifier',
                                              `log_type` int DEFAULT '-1' COMMENT 'log type: -1 deduct, 1 restore',
                                              `business_type` int DEFAULT '1' COMMENT 'business type: 1 order created successfully, 2 order creation timed out, 3 order creation failed',
                                              `reconciliation_status` int NOT NULL DEFAULT '1' COMMENT 'Reconciliation status: 1 pending; 2 abnormal; 3 inconsistent; 4 consistent',
                                              `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time',
                                              `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_order_id` (`order_id`) USING BTREE,
                                              KEY `idx_message_id` (`message_id`) USING BTREE,
                                              KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_voucher_reconcile_log_1`
--

LOCK TABLES `tb_voucher_reconcile_log_1` WRITE;
/*!40000 ALTER TABLE `tb_voucher_reconcile_log_1` DISABLE KEYS */;
/*!40000 ALTER TABLE `tb_voucher_reconcile_log_1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'hmdp_1'
--
-- Dump completed on 2025-11-24 10:09:29
