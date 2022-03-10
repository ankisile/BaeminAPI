package com.example.demo.src.likes;

import com.example.demo.src.likes.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class LikesDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int checkStore(int storeId){
        String checkStoreQuery = "select(exists(select * from Restaurant where restaurantID = ?))";
        int checkStoreParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreQuery, int.class, checkStoreParams);
    }

    public int checkLikes(int userId, int storeId){
        String checkLikesQuery = "select(exists(select * from LikeStore where userIdx = ? and restaurantID = ?))";
        Object[] checkLikesParams = new Object[]{userId, storeId};
        return this.jdbcTemplate.queryForObject(checkLikesQuery, int.class, checkLikesParams);
    }

    public int checkLikesByUserId(int userId){
        String checkLikesByUserIdQuery = "select(exists(select * from LikeStore where userIdx = ?))";
        int checkLikesByUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkLikesByUserIdQuery, int.class, checkLikesByUserIdParams);
    }

    public void createLikes(int userId, int storeId){
        String createLikesQuery = "insert into LikeStore(userIdx, restaurantID) values(?, ?)";
        Object[] createLikesParams = new Object[]{userId, storeId};
        this.jdbcTemplate.update(createLikesQuery, createLikesParams);
    }

    public int checkLikesStatus(int userId, int storeId){
        String checkLikesStatusQuery = "select status from LikeStore where userIdx = ? and restaurantID = ?";
        Object[] checkLikesStatusParams = new Object[]{userId, storeId};
        return this.jdbcTemplate.queryForObject(checkLikesStatusQuery, int.class, checkLikesStatusParams);
    }

    public void activeLikesStatus(int userId, int storeId){
        String activeLikesStatusQuery = "update LikeStore set status = 1 where userIdx = ? and restaurantID = ?";
        Object[] activeLikesStatusParams = new Object[]{userId, storeId};
        this.jdbcTemplate.update(activeLikesStatusQuery, activeLikesStatusParams);
    }

    public void inactiveLikesStatus(int userId, int storeId){
        String inactiveLikesStatusQuery = "update LikeStore set status = 0 where userIdx = ? and restaurantID = ?";
        Object[] inactiveLikesStatusParams = new Object[]{userId, storeId};
        this.jdbcTemplate.update(inactiveLikesStatusQuery, inactiveLikesStatusParams);
    }

    public List<Store> getStores(int userId) {
        String getStoresQuery = "SELECT R.restaurantID as storeId, R.representImg as representImg, R.name as storeName, R.minPrice as minPrice,\n" +
                "                       (case when D.maxTime is not null then concat(D.minTime,'~',D.maxTime) else D.minTime end) as deliveryTime,\n" +
                "                       (case when D.maxTip is not null then concat(D.minTip,'~',D.maxTip) else D.minTip end) as deliveryTip,\n" +
                "                       R.available as available,  if (datediff(  current_timestamp, R.createdAt) <3,'Y','N') as newStore,\n" +
                "                       group_concat( F.name) as popularFood, R.togo as Togo, (case when E.coupon>0 then '쿠폰있음'    else '쿠폰없음'  end) as coupon,\n" +
                "                       CASE WHEN starGrade IS NULL THEN 0 ELSE starGrade END AS rate,\n" +
                "                       CASE WHEN starCount IS NULL THEN 0 ELSE starCount END AS reviewCount\n" +
                "                FROM LikeStore LS\n" +
                "                LEFT JOIN (SELECT restaurantID, name, representImg, minPrice,createdAt,available,togo from Restaurant group by restaurantID) AS R on R.restaurantID = LS.restaurantID\n" +
                "                LEFT JOIN (select restaurantID,minTime, maxTime, minTip, maxTip from Delivery) as D on LS.restaurantID = D.restaurantID\n" +
                "                LEFT JOIN (select restaurantID,name from Food where popular=1) as F on LS.restaurantID = F.restaurantID\n" +
                "                LEFT JOIN (SELECT restaurantID, COUNT(*) as coupon FROM Event GROUP BY restaurantID) as E on LS.restaurantID = E.restaurantID\n" +
                "                LEFT JOIN ( SELECT restaurantID, ROUND(SUM(rate) / COUNT(restaurantID),1) as starGrade, COUNT(restaurantID) as starCount\n" +
                "                            From Review group by restaurantID) as Rv on Rv.restaurantID=LS.restaurantID\n" +
                "                where LS.userIdx=? and LS.status=1\n" +
                "                group by R.name";

        return this.jdbcTemplate.query(getStoresQuery,
                (rs, rowNum) -> new Store(
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
                ),userId);
    }

    public String getTotalLikesCount(int userId){
        String getTotalLikesCountQuery = "select concat('총 ', count(*), '개') from LikeStore where status = 1 and userIdx = ?";
        int getTotalLikesCountParams = userId;
        return this.jdbcTemplate.queryForObject(getTotalLikesCountQuery, String.class, getTotalLikesCountParams);
    }

}