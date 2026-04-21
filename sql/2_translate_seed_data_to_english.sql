-- Apply English seed content to existing local databases without a full re-import.

USE hmdp_0;

START TRANSACTION;

UPDATE tb_shop_type
SET name = CASE id
  WHEN 1 THEN 'Food'
  WHEN 2 THEN 'KTV'
  WHEN 3 THEN 'Beauty & Hair'
  WHEN 4 THEN 'Fitness'
  WHEN 5 THEN 'Massage & Foot Spa'
  WHEN 6 THEN 'Beauty Spa'
  WHEN 7 THEN 'Family Fun'
  WHEN 8 THEN 'Bars'
  WHEN 9 THEN 'Party House'
  WHEN 10 THEN 'Lashes & Nails'
END
WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

UPDATE tb_shop
SET
  name = CASE id
    WHEN 1 THEN '103 Tea Cafe'
    WHEN 2 THEN 'Cai Ma Hongtao BBQ · Old Beijing Copper Pot Lamb Hotpot'
    WHEN 3 THEN 'Xin Bailu Restaurant (Canal Shangjie)'
    WHEN 4 THEN 'Mamala (Hangzhou Ledi Harbor)'
    WHEN 5 THEN 'Haidilao Hot Pot (Crystal City Mall)'
    WHEN 6 THEN 'Xingfuli Old Beijing Hotpot (Silian)'
    WHEN 7 THEN 'Luyu Grilled Fish (Gongshu Wanda Plaza)'
    WHEN 8 THEN 'Asakusa House Sushi (Canal Shangjie)'
    WHEN 9 THEN 'Yang Lao San Lamb Spine & Short Rib Charcoal Hotpot (Canal Shangjie)'
    WHEN 10 THEN 'Kelaidi KTV (Canal Shangjie)'
    WHEN 11 THEN 'INLOVE KTV (Crystal City)'
    WHEN 12 THEN 'Mei (Hangzhou Ledi Harbor)'
    WHEN 13 THEN 'Ou K La Party KTV (Beicheng Tiandi)'
    WHEN 14 THEN 'Star Gathering KTV (Gongshu Wanda)'
  END,
  area = CASE id
    WHEN 1 THEN 'Daguan'
    WHEN 2 THEN 'Gongchen Bridge / Shangtang'
    WHEN 3 THEN 'Canal Shangjie'
    WHEN 4 THEN 'Gongchen Bridge / Shangtang'
    WHEN 5 THEN 'Daguan'
    WHEN 6 THEN 'Gongchen Bridge / Shangtang'
    WHEN 7 THEN 'North New Town'
    WHEN 8 THEN 'Canal Shangjie'
    WHEN 9 THEN 'Canal Shangjie'
    WHEN 10 THEN 'Canal Shangjie'
    WHEN 11 THEN 'Crystal City'
    WHEN 12 THEN 'Ledi Harbor'
    WHEN 13 THEN 'D32 Tianyang Shopping Center'
    WHEN 14 THEN 'North New Town'
  END,
  address = CASE id
    WHEN 1 THEN 'No. 29, Jinchang Wenyuan, Jinhua Road'
    WHEN 2 THEN 'No. 1035 Shangtang Road (next to ICBC)'
    WHEN 3 THEN 'F5, Canal Shangjie Shopping Center, No. 2 Taizhou Road'
    WHEN 4 THEN 'B115, Level 1, Phase 2, Ledi Harbor Mall, No. 66 Lishui Road'
    WHEN 5 THEN 'F6, Crystal City Shopping Center, No. 458 Shangtang Road'
    WHEN 6 THEN 'No. 166 Silian, No. 189 Jinhua South Road'
    WHEN 7 THEN 'Room 409, Unit 2, Building 4, Wanda Commercial Center, No. 666 Hangxing Road (Shop 4005)'
    WHEN 8 THEN 'B1, Canal Shangjie, No. 80 Jinhua Road, Gongshu District'
    WHEN 9 THEN 'F5, Canal Shangjie Shopping Center, No. 2 Taizhou Road'
    WHEN 10 THEN 'F4, Canal Shangjie Shopping Center, No. 2 Taizhou Road'
    WHEN 11 THEN 'Level 6, Crystal City Shopping Center, No. 458 Shangtang Road'
    WHEN 12 THEN 'F4, Ledi Harbor, No. 58 Lishui Road'
    WHEN 13 THEN 'Level 5, Beicheng Tiandi, No. 567 Huzhou Street'
    WHEN 14 THEN 'Levels 1-2, Tower C, Wanda Plaza, No. 666 Hangxing Road'
  END
WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

UPDATE tb_blog
SET
  title = CASE id
    WHEN 4 THEN 'An endless romantic night | sipping red wine among the flowers with a tomahawk steak 🍷🥩'
    WHEN 5 THEN 'Around ¥30 per person — I am obsessed with this Hong Kong-style cafe in Hangzhou‼️'
    WHEN 6 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
    WHEN 7 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
  END,
  content = CASE id
    WHEN 4 THEN 'Life is half fireworks, half poetry.<br/>When everyday life feels dull, make your own little ceremony 🍒<br/><br/>🏰「Xiaozhuli · Secret Romantic Garden Restaurant」🏰<br/><br/>A dreamy garden-style western restaurant filled with flowers, candlelight, and wine. The whole space feels soft, romantic, and perfect for photos.<br/><br/>📍Address: No. 200 Yan''an Road (across from Carrefour)<br/>🚌Metro: Exit B, Ding''an Road Station on Line 1. Turn right through the passage and you will see it.<br/><br/>Recommended dishes:<br/>「Tomahawk Steak」Juicy, tender, and full of smoky aroma.<br/>「Creamy Bacon Pasta」Rich, creamy, and seriously comforting.<br/>「Sea Bass with Cilantro Sauce」Tender fish with a bold spicy kick.<br/><br/>Service was attentive, and the staff were happy to help with recommendations and photos.<br/>If you want flowers, wine, and a relaxed date-night mood in one place, this one is worth visiting 🌸'
    WHEN 5 THEN 'Another great Hong Kong-style cafe in Hangzhou 🍴 Great for photos, easy on the wallet, and packed with nostalgic TVB vibes 📺📷<br/><br/>Shop: Jiuji Ice House (Ledi Harbor)<br/>Address: B1, Ledi Harbor, Lishui Road, Hangzhou (next to the skating rink)<br/><br/>Best picks:<br/>✔️Soul-Satisfying BBQ Rice (¥38) Sweet char siu, jammy eggs, and rich sauce over rice.<br/>✔️Causeway Bay Lava French Toast (¥28) Crispy outside with a flowing creamy filling.<br/>✔️Heavenly Hong Kong French Toast (¥16) Buttery, soft inside, and finished with condensed milk.<br/>✔️Nostalgic Sweet-and-Sour Fried Egg Rice (¥28) Fluffy eggs, chicken cutlet, and sweet-sour sauce.<br/>✔️BBQ Combo Platter (¥66) Roast goose and char siu with authentic Cantonese flavor.<br/>✔️Braised Crispy Pigeon (¥18.8) Crisp skin and satisfying texture.<br/>✔️Hong Kong Bear Silk Stocking Milk Tea (¥19) Cute, photogenic, and fragrant.<br/><br/>If you want a budget-friendly Hong Kong cafe with fun visuals and comforting food, this place is a solid choice.'
    WHEN 6 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
    WHEN 7 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
  END
WHERE id IN (4, 5, 6, 7);

COMMIT;

USE hmdp_1;

START TRANSACTION;

UPDATE tb_shop_type
SET name = CASE id
  WHEN 1 THEN 'Food'
  WHEN 2 THEN 'KTV'
  WHEN 3 THEN 'Beauty & Hair'
  WHEN 4 THEN 'Fitness'
  WHEN 5 THEN 'Massage & Foot Spa'
  WHEN 6 THEN 'Beauty Spa'
  WHEN 7 THEN 'Family Fun'
  WHEN 8 THEN 'Bars'
  WHEN 9 THEN 'Party House'
  WHEN 10 THEN 'Lashes & Nails'
END
WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

UPDATE tb_shop
SET
  name = CASE id
    WHEN 1 THEN '103 Tea Cafe'
    WHEN 2 THEN 'Cai Ma Hongtao BBQ · Old Beijing Copper Pot Lamb Hotpot'
    WHEN 3 THEN 'Xin Bailu Restaurant (Canal Shangjie)'
    WHEN 4 THEN 'Mamala (Hangzhou Ledi Harbor)'
    WHEN 5 THEN 'Haidilao Hot Pot (Crystal City Mall)'
    WHEN 6 THEN 'Xingfuli Old Beijing Hotpot (Silian)'
    WHEN 7 THEN 'Luyu Grilled Fish (Gongshu Wanda Plaza)'
    WHEN 8 THEN 'Asakusa House Sushi (Canal Shangjie)'
    WHEN 9 THEN 'Yang Lao San Lamb Spine & Short Rib Charcoal Hotpot (Canal Shangjie)'
    WHEN 10 THEN 'Kelaidi KTV (Canal Shangjie)'
    WHEN 11 THEN 'INLOVE KTV (Crystal City)'
    WHEN 12 THEN 'Mei (Hangzhou Ledi Harbor)'
    WHEN 13 THEN 'Ou K La Party KTV (Beicheng Tiandi)'
    WHEN 14 THEN 'Star Gathering KTV (Gongshu Wanda)'
  END,
  area = CASE id
    WHEN 1 THEN 'Daguan'
    WHEN 2 THEN 'Gongchen Bridge / Shangtang'
    WHEN 3 THEN 'Canal Shangjie'
    WHEN 4 THEN 'Gongchen Bridge / Shangtang'
    WHEN 5 THEN 'Daguan'
    WHEN 6 THEN 'Gongchen Bridge / Shangtang'
    WHEN 7 THEN 'North New Town'
    WHEN 8 THEN 'Canal Shangjie'
    WHEN 9 THEN 'Canal Shangjie'
    WHEN 10 THEN 'Canal Shangjie'
    WHEN 11 THEN 'Crystal City'
    WHEN 12 THEN 'Ledi Harbor'
    WHEN 13 THEN 'D32 Tianyang Shopping Center'
    WHEN 14 THEN 'North New Town'
  END,
  address = CASE id
    WHEN 1 THEN 'No. 29, Jinchang Wenyuan, Jinhua Road'
    WHEN 2 THEN 'No. 1035 Shangtang Road (next to ICBC)'
    WHEN 3 THEN 'F5, Canal Shangjie Shopping Center, No. 2 Taizhou Road'
    WHEN 4 THEN 'B115, Level 1, Phase 2, Ledi Harbor Mall, No. 66 Lishui Road'
    WHEN 5 THEN 'F6, Crystal City Shopping Center, No. 458 Shangtang Road'
    WHEN 6 THEN 'No. 166 Silian, No. 189 Jinhua South Road'
    WHEN 7 THEN 'Room 409, Unit 2, Building 4, Wanda Commercial Center, No. 666 Hangxing Road (Shop 4005)'
    WHEN 8 THEN 'B1, Canal Shangjie, No. 80 Jinhua Road, Gongshu District'
    WHEN 9 THEN 'F5, Canal Shangjie Shopping Center, No. 2 Taizhou Road'
    WHEN 10 THEN 'F4, Canal Shangjie Shopping Center, No. 2 Taizhou Road'
    WHEN 11 THEN 'Level 6, Crystal City Shopping Center, No. 458 Shangtang Road'
    WHEN 12 THEN 'F4, Ledi Harbor, No. 58 Lishui Road'
    WHEN 13 THEN 'Level 5, Beicheng Tiandi, No. 567 Huzhou Street'
    WHEN 14 THEN 'Levels 1-2, Tower C, Wanda Plaza, No. 666 Hangxing Road'
  END
WHERE id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

UPDATE tb_blog
SET
  title = CASE id
    WHEN 4 THEN 'An endless romantic night | sipping red wine among the flowers with a tomahawk steak 🍷🥩'
    WHEN 5 THEN 'Around ¥30 per person — I am obsessed with this Hong Kong-style cafe in Hangzhou‼️'
    WHEN 6 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
    WHEN 7 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
  END,
  content = CASE id
    WHEN 4 THEN 'Life is half fireworks, half poetry.<br/>When everyday life feels dull, make your own little ceremony 🍒<br/><br/>🏰「Xiaozhuli · Secret Romantic Garden Restaurant」🏰<br/><br/>A dreamy garden-style western restaurant filled with flowers, candlelight, and wine. The whole space feels soft, romantic, and perfect for photos.<br/><br/>📍Address: No. 200 Yan''an Road (across from Carrefour)<br/>🚌Metro: Exit B, Ding''an Road Station on Line 1. Turn right through the passage and you will see it.<br/><br/>Recommended dishes:<br/>「Tomahawk Steak」Juicy, tender, and full of smoky aroma.<br/>「Creamy Bacon Pasta」Rich, creamy, and seriously comforting.<br/>「Sea Bass with Cilantro Sauce」Tender fish with a bold spicy kick.<br/><br/>Service was attentive, and the staff were happy to help with recommendations and photos.<br/>If you want flowers, wine, and a relaxed date-night mood in one place, this one is worth visiting 🌸'
    WHEN 5 THEN 'Another great Hong Kong-style cafe in Hangzhou 🍴 Great for photos, easy on the wallet, and packed with nostalgic TVB vibes 📺📷<br/><br/>Shop: Jiuji Ice House (Ledi Harbor)<br/>Address: B1, Ledi Harbor, Lishui Road, Hangzhou (next to the skating rink)<br/><br/>Best picks:<br/>✔️Soul-Satisfying BBQ Rice (¥38) Sweet char siu, jammy eggs, and rich sauce over rice.<br/>✔️Causeway Bay Lava French Toast (¥28) Crispy outside with a flowing creamy filling.<br/>✔️Heavenly Hong Kong French Toast (¥16) Buttery, soft inside, and finished with condensed milk.<br/>✔️Nostalgic Sweet-and-Sour Fried Egg Rice (¥28) Fluffy eggs, chicken cutlet, and sweet-sour sauce.<br/>✔️BBQ Combo Platter (¥66) Roast goose and char siu with authentic Cantonese flavor.<br/>✔️Braised Crispy Pigeon (¥18.8) Crisp skin and satisfying texture.<br/>✔️Hong Kong Bear Silk Stocking Milk Tea (¥19) Cute, photogenic, and fragrant.<br/><br/>If you want a budget-friendly Hong Kong cafe with fun visuals and comforting food, this place is a solid choice.'
    WHEN 6 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
    WHEN 7 THEN 'Hangzhou weekend idea | Horseback riding for just ¥50 🐎'
  END
WHERE id IN (4, 5, 6, 7);

UPDATE tb_user_0
SET nick_name = CASE id
  WHEN 1987041610793484289 THEN 'Little Fish'
  WHEN 1987042234935279617 THEN 'Coco Goes Meatless Today'
  WHEN 1987042505555968001 THEN 'Cutie Pie'
END
WHERE id IN (1987041610793484289, 1987042234935279617, 1987042505555968001);

UPDATE tb_voucher_0
SET
  title = '¥80 Cash Voucher',
  sub_title = 'Valid Monday to Sunday',
  rules = 'No special rules'
WHERE id = 1;

COMMIT;
