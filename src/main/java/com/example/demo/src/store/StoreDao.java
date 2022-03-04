package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetStoreRes> getStores(int categoryId) {
        String getStoresQuery = "SELECT R.restaurantID as storeId, R.representImg as representImg, R.name as storeName, R.minPrice as minPrice,\n" +
                "       (case when D.maxTime is not null then concat(D.minTime,'~',D.maxTime) else D.minTime end) as deliveryTime,\n" +
                "       (case when D.maxTip is not null then concat(D.minTip,'~',D.maxTip) else D.minTip end) as deliveryTip,\n" +
                "       R.available as available,  if (datediff(  current_timestamp, R.createdAt) <3,'Y','N') as newStore,\n" +
                "       group_concat( F.name) as popularFood, R.togo as Togo, (case when E.coupon>0 then '쿠폰있음'    else '쿠폰없음'  end) as coupon,\n" +
                "       CASE WHEN starGrade IS NULL THEN 0 ELSE starGrade END AS rate,\n" +
                "       CASE WHEN starCount IS NULL THEN 0 ELSE starCount END AS reviewCount\n" +
                "        FROM Category C\n" +
                "            LEFT JOIN (SELECT restaurantID, name, representImg, minPrice,createdAt,available,togo,categoryId from Restaurant group by restaurantID) AS R on R.categoryId=C.id\n" +
                "            LEFT JOIN (select restaurantID,minTime, maxTime, minTip, maxTip from Delivery) as D on R.restaurantID = D.restaurantID\n" +
                "                LEFT JOIN (select restaurantID,name from Food where popular=1) as F on R.restaurantID = F.restaurantID\n" +
                "                    LEFT JOIN (SELECT restaurantID, COUNT(*) as coupon FROM Event GROUP BY restaurantID) as E on R.restaurantID = E.restaurantID\n" +
                "                LEFT JOIN ( SELECT restaurantID, ROUND(SUM(rate) / COUNT(restaurantID),1) as starGrade, COUNT(restaurantID) as starCount\n" +
                "                            From Review group by restaurantID) as Rv on Rv.restaurantID=R.restaurantID\n" +
                "                where C.id= ?\n" +
                "                group by R.name";
        int getStoresParams = categoryId;
        return this.jdbcTemplate.query(getStoresQuery,
                (rs, rowNum) -> new GetStoreRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getInt("minPrice"),
                        rs.getString("deliveryTime"),
                        rs.getString("deliveryTip"),
                        rs.getString("representImg"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewCount"),
                        rs.getString("coupon"),
                        rs.getString("togo"),
                        rs.getString("popularFood"),
                        rs.getString("available"),
                        rs.getString("newStore")
                ),
                categoryId);
    }


    public List<GetStoreImgRes> getStoreImages(int storeId){
        String getStoreImagesQuery = "select imgUrl from RestaurantImg where restaurantID = ? ";
        int getStoreImagesParams = storeId;
        return this.jdbcTemplate.query(getStoreImagesQuery,
                (rs, rowNum) -> new GetStoreImgRes(
                        rs.getString("imgUrl")),
                storeId);
    }

    public int checkStoreId(int storeId) {
        String checkStoreIdQuery = "select(exists(select * from Restaurant where restaurantID = ?))";
        int checkStoreIdParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreIdQuery, int.class, checkStoreIdParams);
    }

}