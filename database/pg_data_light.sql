--
-- PostgreSQL database dump
--

-- Dumped from database version 13.2 (Debian 13.2-1.pgdg100+1)
-- Dumped by pg_dump version 13.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: dev
--

COPY public.users (id) FROM stdin;
42
\.


--
-- Data for Name: dictionaries; Type: TABLE DATA; Schema: public; Owner: dev
--

COPY public.dictionaries (id, target_lang, name, source_lang, user_id) FROM stdin;
3	RU	Weather	EN	42
\.


--
-- Data for Name: cards; Type: TABLE DATA; Schema: public; Owner: dev
--

COPY public.cards (id, text, answered, details, part_of_speech, status, transcription, dictionary_id) FROM stdin;
1878	atmospheric	0	parsed from lingvo xml	ADJECTIVE	NEW	ˌætməs'ferik	3
1879	weather	0	parsed from lingvo xml	NOUN	NEW	'weðə	3
1880	snow	0	parsed from lingvo xml	NOUN	NEW	snəu	3
1881	rain	0	parsed from lingvo xml	NOUN	NEW	rein	3
1882	wind	0	parsed from lingvo xml	NOUN	NEW	wind	3
1883	hail	0	parsed from lingvo xml	NOUN	NEW	heil	3
1884	thunderstorm	0	parsed from lingvo xml	NOUN	NEW	'θʌndəstɔːm	3
1885	precipitation	0	parsed from lingvo xml	NOUN	NEW	priˌsipi'teiʃ(ə)n	3
1886	thunder	0	parsed from lingvo xml	NOUN	NEW	'θʌndə	3
1887	lightning	0	parsed from lingvo xml	NOUN	NEW	'laitniŋ	3
1888	anticyclone	0	parsed from lingvo xml	NOUN	NEW	'ænti'saikləun	3
1889	atmosphere	0	parsed from lingvo xml	NOUN	NEW	'ætməsfiə	3
1890	barometer	0	parsed from lingvo xml	NOUN	NEW	bə'rɔmitə	3
1891	windy	0	parsed from lingvo xml	ADJECTIVE	NEW	'windi	3
1892	humidity	0	parsed from lingvo xml	NOUN	NEW	hjuː'midəti	3
1893	humid	0	parsed from lingvo xml	ADJECTIVE	NEW	'hjuːmid	3
1894	moist	0	parsed from lingvo xml	ADJECTIVE	NEW	mɔist	3
1895	damp	0	parsed from lingvo xml	ADJECTIVE	NEW	dæmp	3
1896	sunrise	0	parsed from lingvo xml	NOUN	NEW	'sʌnraiz	3
1897	degree	0	parsed from lingvo xml	NOUN	NEW	di'griː	3
1898	pressure	0	parsed from lingvo xml	NOUN	NEW	'preʃə	3
1899	stuffy	0	parsed from lingvo xml	ADJECTIVE	NEW	'stʌfi	3
1900	heat	0	parsed from lingvo xml	NOUN	NEW	hiːt	3
1901	hot	0	parsed from lingvo xml	ADJECTIVE	NEW	hɔt	3
1902	sunset	0	parsed from lingvo xml	NOUN	NEW	'sʌnset	3
1903	frost	0	parsed from lingvo xml	NOUN	NEW	frɔst	3
1904	drizzle	0	parsed from lingvo xml	NOUN	NEW	'drizl	3
1905	haze	0	parsed from lingvo xml	NOUN	NEW	heiz	3
1906	ice	0	parsed from lingvo xml	NOUN	NEW	ais	3
1907	shower	0	parsed from lingvo xml	NOUN	NEW	'ʃəuə	3
1908	puddle	0	parsed from lingvo xml	NOUN	NEW	'pʌdl	3
1909	meteorology	0	parsed from lingvo xml	NOUN	NEW	ˌmiːti(ə)'rɔləʤi	3
1910	cloudy	0	parsed from lingvo xml	ADJECTIVE	NEW	'klaudi	3
1911	thaw	0	parsed from lingvo xml	NOUN	NEW	θɔː	3
1912	cool	0	parsed from lingvo xml	ADJECTIVE	NEW	kuːl	3
1913	dusty	0	parsed from lingvo xml	ADJECTIVE	NEW	'dʌsti	3
1914	rainbow	0	parsed from lingvo xml	NOUN	NEW	'reinbəu	3
1915	fresh	0	parsed from lingvo xml	ADJECTIVE	NEW	freʃ	3
1916	slippery	0	parsed from lingvo xml	ADJECTIVE	NEW	'slipəri	3
1917	slush	0	parsed from lingvo xml	NOUN	NEW	slʌʃ	3
1918	smog	0	parsed from lingvo xml	NOUN	NEW	smɔg	3
1919	snowfall	0	parsed from lingvo xml	NOUN	NEW		3
1920	sunny	0	parsed from lingvo xml	ADJECTIVE	NEW	'sʌni	3
1921	dry	0	parsed from lingvo xml	ADJECTIVE	NEW	drai	3
1922	wet	0	parsed from lingvo xml	ADJECTIVE	NEW	wet	3
1923	temperature	0	parsed from lingvo xml	NOUN	NEW	'tempriʧə	3
1924	warm	0	parsed from lingvo xml	ADJECTIVE	NEW	wɔːm	3
1925	thermometer	0	parsed from lingvo xml	NOUN	NEW	θə'mɔmitə	3
1926	fog	0	parsed from lingvo xml	NOUN	NEW	fɔg	3
1927	mist	0	parsed from lingvo xml	NOUN	NEW	mist	3
1928	foggy	0	parsed from lingvo xml	ADJECTIVE	NEW	'fɔgi	3
1929	water	0	parsed from lingvo xml	NOUN	NEW	'wɔːtə	3
1930	cold	0	parsed from lingvo xml	ADJECTIVE	NEW	kəuld	3
1931	cyclone	0	parsed from lingvo xml	NOUN	NEW	'saikləun	3
1932	storm	0	parsed from lingvo xml	NOUN	NEW	stɔːm	3
1933	clear	0	parsed from lingvo xml	ADJECTIVE	NEW	kliə	3
1934	sleet	0	parsed from lingvo xml	NOUN	NEW	sliːt	3
1935	cloud	0	parsed from lingvo xml	NOUN	NEW	klaud	3
1936	chilly	0	parsed from lingvo xml	ADJECTIVE	NEW	'ʧili	3
1937	downpour	0	parsed from lingvo xml	NOUN	NEW	'daunpɔː	3
1938	gale	0	parsed from lingvo xml	NOUN	NEW	geil	3
1939	scorching	0	parsed from lingvo xml	ADJECTIVE	NEW	'skɔːʧiŋ	3
1940	dust	0	parsed from lingvo xml	NOUN	NEW	dʌst	3
1941	blustery	0	parsed from lingvo xml	ADJECTIVE	NEW	'blʌstəri	3
1942	overcast	0	parsed from lingvo xml	ADJECTIVE	NEW	'əuvəkɑːst	3
\.


--
-- Data for Name: examples; Type: TABLE DATA; Schema: public; Owner: dev
--

COPY public.examples (id, text, card_id) FROM stdin;
1290	atmospheric instability -- атмосферная нестабильность	1878
1291	atmospheric front -- атмосферный фронт	1878
1292	atmospheric layer -- слой атмосферы	1878
1293	spell of cold weather -- похолодание	1879
1294	weather forecast -- прогноз погоды	1879
1295	nasty weather -- ненастная погода	1879
1296	weather bureau -- бюро погоды	1879
1297	snow depth -- высота снежного покрова	1880
1298	It snows. -- Идет снег.	1880
1299	a flake of snow -- снежинка	1880
1300	It rains. -- Идет дождь.	1881
1301	drizzling rain -- изморось	1881
1302	heavy rain -- проливной дождь, ливень	1881
1303	torrential rain -- проливной дождь	1881
1304	a sudden gust of wind — внезапный порыв ветра	1882
1305	It hails. -- Идет град.	1883
1306	It was thundering all night long. -- Всю ночь гремел гром.	1886
1307	a flash of lightning -- вспышка молнии	1887
1308	The barometer is falling. -- Барометр падает.	1890
1309	windy weather -- ветреная погода	1891
1310	It is windy. -- Ветрено.	1891
1311	relative humidity -- относительная влажность	1892
1312	damp air -- влажный воздух	1895
1313	5 degrees above (below) zero -- 5 градусов выше (ниже) нуля	1897
1314	atmospheric pressure -- атмосферное давление	1898
1315	low pressure area -- область пониженного давления	1898
1316	hot weather -- жаркая погода	1901
1317	It is hot. -- Жарко.	1901
1318	hoar-frost -- иней, изморозь	1903
1319	It drizzles. -- Идёт мелкий дождь.	1904
1320	heat haze -- марево	1905
1321	thin ice -- тонкий лед	1906
1322	scattered showers — местами проливные дожди	1907
1323	It is cloudy. -- Облачно.	1910
1324	The snow started to thaw. -- Снег начал таять.	1911
1325	cool breeze -- прохладный ветерок	1912
1326	dusty road -- пыльная дорога	1913
1327	fresh air -- свежий воздух	1915
1328	slippery road -- скользкая дорога	1916
1329	sunny day -- солнечный день	1920
1330	dry air -- сухой воздух	1921
1331	What’s the temperature today? -- Какая сегодня температура?	1923
1332	Celsius / Centigrade thermometer -- термометр Цельсия / со шкалой Цельсия	1925
1333	Fahrenheit thermometer -- термометр Фаренгейта / со шкалой Фаренгейта	1925
1334	thick fog -- густой туман	1926
1335	morning mist -- утренний туман, утренняя дымка	1927
1336	cold weather -- холодная погода	1930
1337	dust storm -- пыльная буря	1932
1338	The sky was clear. -- Небо было безоблачным.	1933
1339	gale warning -- штормовое предупреждение	1938
1340	scorching day -- знойный день	1939
1341	scorching sun -- палящее солнце	1939
1342	blustery wind -- порывистый ветер	1941
1343	blustery weather -- ветреная погода	1941
1344	The sky is overcast. -- Небо затянуто облаками.	1942
\.


--
-- Data for Name: translations; Type: TABLE DATA; Schema: public; Owner: dev
--

COPY public.translations (id, text, card_id) FROM stdin;
3342	атмосферный	1878
3343	погода	1879
3344	снег	1880
3345	дождь	1881
3346	ветер	1882
3347	град	1883
3348	гроза	1884
3349	осадки	1885
3350	гром	1886
3351	молния	1887
3352	антициклон	1888
3353	атмосфера	1889
3354	барометр	1890
3355	ветреный	1891
3356	влажность	1892
3357	мокрый	1893
3358	сырой	1893
3359	влажный	1893
3360	сырой	1894
3361	мокрый	1894
3362	влажный	1894
3363	влажный	1895
3364	сырой	1895
3365	восход солнца	1896
3366	градус	1897
3367	давление	1898
3368	душный	1899
3369	жара	1900
3370	жаркий	1901
3371	закат	1902
3372	мороз	1903
3373	заморозки	1903
3374	изморось	1904
3375	мелкий дождь	1904
3376	лёгкий туман	1905
3377	дымка	1905
3378	мгла	1905
3379	лед	1906
3380	ливень	1907
3381	лужа	1908
3382	метеорология	1909
3383	облачный	1910
3384	оттепель	1911
3385	прохладный	1912
3386	пыльный	1913
3387	радуга	1914
3388	свежий	1915
3389	чистый	1915
3390	скользкий	1916
3391	слякоть	1917
3392	смог	1918
3393	густой туман с копотью	1918
3394	снегопад	1919
3395	солнечный	1920
3396	сухой	1921
3397	сырой	1922
3398	влажный	1922
3399	температура	1923
3400	теплый	1924
3401	термометр	1925
3402	градусник	1925
3403	туман	1926
3404	пасмурность	1927
3405	туман	1927
3406	дымка	1927
3407	туманный	1928
3408	вода	1929
3409	холодный	1930
3410	циклон	1931
3411	шторм	1932
3412	гроза	1932
3413	ураган	1932
3414	буря	1932
3415	безоблачный	1933
3416	ясный	1933
3417	дождь со снегом	1934
3418	туча	1935
3419	облако	1935
3420	промозглый	1936
3421	ливень	1937
3422	шторм	1938
3423	сильный ветер	1938
3424	знойный	1939
3425	палящий	1939
3426	пыль	1940
3427	ветреный	1941
3428	порывистый	1941
3429	покрытый облаками	1942
\.


--
-- Name: cards_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dev
--

SELECT pg_catalog.setval('public.cards_id_seq', 1942, true);


--
-- Name: dictionaries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dev
--

SELECT pg_catalog.setval('public.dictionaries_id_seq', 3, true);


--
-- Name: examples_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dev
--

SELECT pg_catalog.setval('public.examples_id_seq', 1344, true);


--
-- Name: translations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dev
--

SELECT pg_catalog.setval('public.translations_id_seq', 3429, true);


--
-- PostgreSQL database dump complete
--

