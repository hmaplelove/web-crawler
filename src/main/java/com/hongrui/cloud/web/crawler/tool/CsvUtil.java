package com.hongrui.cloud.web.crawler.tool;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CsvUtil {
	public static final String filePath = "src/main/resources/template.csv";
	public static final String basePath = "src/main/resources/results";

	 public static String[]  readeCsvHeaders(){  
		 String[] headers=null;
         try {      
              CsvReader reader = new CsvReader(filePath,',',Charset.forName("UTF-8"));
              reader.readHeaders();
              headers=reader.getHeaders();
         }catch(Exception ex){  
             System.out.println(ex);  
         }  
         return headers;
     }  
	 
	public static void write(String fileName,List<Map<String, Object>> data) throws Exception{
		File file=new File(basePath);
		if (!file.exists()) {
			file.mkdir();
		}
		fileName=basePath+"/"+fileName+".csv";
		file=new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
        try {
            CsvWriter csvWriter = new CsvWriter(fileName,',', Charset.forName("GBK"));
            // 写表头
            String[] headers = readeCsvHeaders();
            List<String> src= Arrays.asList(headers);
            List<String> dest=new ArrayList<String>();
            for (String header : src) {
        	   dest.add(header);
            }
            dest.add("bought");
            dest.add("reminder");
            dest.add("ratings");
            headers=dest.toArray(headers);
            String[] content = null;
            csvWriter.writeRecord(headers);
            for (Map<String, Object> map : data) {
            	content =new String[]{
            			map.get("id")==null?"":map.get("id").toString(),
            			map.get("category")==null?"":map.get("category").toString(),
            			map.get("title")==null?"":map.get("title").toString(),
            			map.get("description")==null?"":map.get("description").toString(),
            			map.get("condition")==null?"":map.get("condition").toString(),
            			map.get("product_brand")==null?"":map.get("product_brand").toString(),
            			map.get("product_material")==null?"":map.get("product_material").toString(),
            			map.get("id")==null?"":map.get("id").toString(),
            			map.get("size")==null?"":map.get("size").toString(),
            			map.get("product_color")==null?"":map.get("product_color").toString(),
            			map.get("quantity")==null?"":map.get("quantity").toString(),
            			map.get("starting_bid_amount")==null?"":map.get("starting_bid_amount").toString(),
            			map.get("buy_now_price")==null?"":map.get("buy_now_price").toString(),
            			map.get("retail_price")==null?"":map.get("retail_price").toString(),
            			map.get("target_price")==null?"":map.get("target_price").toString(),
            			map.get("shipping_price")==null?"":map.get("shipping_price").toString(),
            			map.get("country")==null?"":map.get("country").toString(),
            			map.get("shipping_weight_in_ounces")==null?"":map.get("shipping_weight_in_ounces").toString(),
            			map.get("days_to_process_order")==null?"":map.get("days_to_process_order").toString(),
            			map.get("days_to_deliver")==null?"":map.get("days_to_deliver").toString(),
            			map.get("expedited_shipping_price")==null?"":map.get("expedited_shipping_price").toString(),
            			map.get("expedited_delivery_time")==null?"":map.get("expedited_delivery_time").toString(),
            			map.get("shipping_price_other")==null?"":map.get("shipping_price_other").toString(),
            			map.get("accessory_price")==null?"":map.get("accessory_price").toString(),
            			map.get("accessory_description")==null?"":map.get("accessory_description").toString(),
            			map.get("main_image")==null?"":map.get("main_image").toString(),
            			map.get("img_url_1")==null?"":map.get("img_url_1").toString(),
            			map.get("img_url_2")==null?"":map.get("img_url_2").toString(),
            			map.get("img_url_3")==null?"":map.get("img_url_3").toString(),
            			map.get("img_url_4")==null?"":map.get("img_url_4").toString(),
            			"Enabled",
            			map.get("bought")==null?"":map.get("bought").toString(),
            			map.get("reminder")==null?"":map.get("reminder").toString(),
            			map.get("ratings")==null?"":map.get("ratings").toString()
            	};
            	csvWriter.writeRecord(content);
			}
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static void main(String[] args) throws Exception {
		write("测试", null);
	}
}
