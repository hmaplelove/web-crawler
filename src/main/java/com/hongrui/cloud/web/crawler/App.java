package com.hongrui.cloud.web.crawler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hongrui.cloud.web.crawler.tool.CsvUtil;
import com.hongrui.cloud.web.crawler.tool.HttpUtil;
public class App {

	private static Map<String, Object> map=new  HashMap<String, Object>();
	static{
		//map.put("Jewelry", "9200");
		//map.put("Rings", "9201");
		//map.put("Necklaces", "9201");
		//map.put("Bracelets", "9203");
		//map.put("Earrings", "9204");
		map.put("Other", "9205");
		//map.put("Sets", "9206");
	}
	
	public static void main(String[] args) throws Exception {
		for (Entry<String, Object> entry : map.entrySet()) {
			String catalog=entry.getKey();
			String id=entry.getValue().toString();
			List<Map<String, Object>> values=getJewelryByCatalog(id,catalog);
			System.out.println("get #"+catalog+"# ===> 【"+values.size()+"】 ok!");
			map.put(catalog, values);
			exportCsv(catalog,values);
			
		}
	}

	private static void exportCsv(String catalog,List<Map<String, Object>> values) {
		for (Map<String, Object>  map: values) {
			for (Entry<String, Object> entry : map.entrySet()) {
				Object value=entry.getValue()==null? "":entry.getValue();
				map.put(entry.getKey(),value);
			}
		}
		try {
			CsvUtil.write(catalog, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Map<String, Object>> getJewelryByCatalog(String catalogId,String catalog) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(100);
		List<Map<String,Object>> jewelrys=new ArrayList<Map<String, Object>>();
		List<Future<List<Map<String, Object>>>> list = new ArrayList<Future<List<Map<String, Object>>>>();
		for (int i = 1; i <=100; i++) {
			Future<List<Map<String, Object>>> future=pool.submit(new Task(catalogId, catalog, i) );
			list.add(future);
		}
		pool.shutdown();
		
		for (Future<List<Map<String, Object>>> f : list) {
			jewelrys.addAll(f.get());
	    }
	    return jewelrys;
	}
    
}

class Task implements Callable<List<Map<String, Object>>> {
    private String catalogId;
    private String catalog;
    private int page;

    Task(String catalogId,String catalog,int page) {
        this.catalogId = catalogId;
        this.catalog = catalog;
        this.page = page;
    }

    public List<Map<String, Object>> call() throws Exception {
    	List<Map<String,Object>> jewelrys=new ArrayList<Map<String, Object>>();
    	String url="https://tophatter.com/catalogs/"+catalogId+"?page="+page;
		System.out.println("[send base url]====>"+url);
		String html=HttpUtil.getData(url);
	    Document document = Jsoup.parse(html);
	    Elements elements=document.getElementsByAttributeValue("data-catalog-id", catalogId);
	    if (!elements.isEmpty()) {
	    	int count=0;
	    	for (Element element : elements) {
	    		count++;
	    		Map<String, Object> data=new HashMap<String, Object>();
		    	Element img=element.getElementsByAttribute("data-lot-id").get(0);
		    	String id=img.attr("data-lot-id");
		    	String bought="0";
		    	if (!element.getElementsByClass("bought-this").isEmpty()) {
		    		bought=element.getElementsByClass("bought-this").get(0).text().trim();
					bought=bought.replace(" bought this","");
				}
		    	String reminder="0";
		    	if (!element.getElementsByClass("reminder-count").isEmpty()) {
		    		reminder=element.getElementsByClass("reminder-count").get(0).text().trim();
		    	}
		    	String ratings="0";
		    	if (!element.getElementsByClass("ratings").isEmpty()) {
		    		double star=0;
		    		if (!element.getElementsByClass("ratings").get(0).getElementsByClass("fa fa-star-half-o").isEmpty()) {
		    			star=0.5;
					}
		    		star+=element.getElementsByClass("ratings").get(0).getElementsByClass("fa fa-star").size();
		    		ratings=Double.toString(star);
		    	}
		    	
		    	data.put("id", id);
		    	data.put("bought", bought);
		    	data.put("reminder", reminder);
		    	data.put("ratings", ratings);
		    	data.put("category", "Jewelry | "+catalog);
		    	System.out.println("[page "+page+" ("+count+")】] data==>"+JSON.toJSONString(data));
		    	data.putAll(getDetailById(id));
		    	jewelrys.add(data);
	    	}
	    }	
        return jewelrys;
    }
    
    @SuppressWarnings("unchecked")
	private static Map<String, Object> getDetailById(String id) {
		String url ="https://tophatter.com/api/v1/lots/"+id+"?source=lot-view-catalog";
		//System.out.println("[send detail url]====>"+url);
		String json=HttpUtil.getData(url);
		json=json.replaceAll("thumbnail.jpg", "large.jpg");
		Map<String, Object> data = null;
		try {
			data = JSON.parseObject(json, Map.class);
			JSONArray image_urls=(JSONArray) data.get("image_urls");
			
			int index=0;
			
			for (int i = 0; i < image_urls.size(); i++) {
				if (!image_urls.get(i).toString().trim().equals(data.get("main_image").toString().trim())) {
					data.put("img_url_"+(index++), image_urls.get(i));
				}
			}
			
			for (int i = 1; i < 5; i++) {
				if(!data.containsKey("img_url_"+i)){
					data.put("img_url_"+i, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return data;
	}
}