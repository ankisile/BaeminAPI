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

    public GetStoreInfoRes getStoreInfos(int storeId) {
        String getStoreInfoQuery = "SELECT R.restaurantID as storeId, R.name as storeName,\n" +
                "                       R.phoneNumber as phoneNumber,\n" +
                "                       R.minPrice as minPrice,\n" +
                "                       (case when D.maxTime is not null then concat(D.minTime,'~',D.maxTime)\n" +
                "                            else D.minTime end) as deliveryTime,\n" +
                "                       (case when D.maxTip is not null then concat(D.minTip,'~',D.maxTip)\n" +
                "                            else D.minTip end) as deliveryTip,\n" +
                "                       R.available as available,\n" +
                "                       (case when E.coupon>0 then '쿠폰있음'\n" +
                "                           else '쿠폰없음' end) as coupon,\n" +
                "                       CASE WHEN starGrade IS NULL THEN 0 ELSE starGrade END AS starRate,\n" +
                "                   CASE WHEN starCount IS NULL THEN 0 ELSE starCount END AS reviewCount,\n" +
                "                       cASE WHEN likeCount IS NULL THEN 0 ELSE likeCount END AS likes\n" +
                "                FROM Restaurant R\n" +
                "                LEFT JOIN (select restaurantID,minTime, maxTime, minTip, maxTip from Delivery) as D on R.restaurantID = D.restaurantID\n" +
                "                LEFT JOIN (SELECT restaurantID, COUNT(*) as coupon,eventId FROM Event GROUP BY restaurantID) as E on R.restaurantID = E.restaurantID\n" +
                "                LEFT Join Coupon C on C.eventId=E.eventId\n" +
                "                LEFT JOIN ( SELECT restaurantID, ROUND(SUM(rate) / COUNT(restaurantID),1) as starGrade, COUNT(restaurantID) as starCount\n" +
                "                            From Review group by restaurantID) as Rv on Rv.restaurantID=R.restaurantID\n" +
                "                LEFT JOIN ( SELECT restaurantID, COUNT(*) as likeCount from LikeStore group by restaurantID) as LS on R.restaurantID = LS.restaurantID\n" +
                "                where R.restaurantID= ?";
        int getStoreInfoParams = storeId;
        return this.jdbcTemplate.queryForObject(getStoreInfoQuery,
                (rs, rowNum) -> new GetStoreInfoRes(
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getString("phoneNumber"),
                        rs.getInt("minPrice"),
                        rs.getDouble("starRate"),
                        rs.getString("reviewCount"),
                        rs.getString("deliveryTime"),
                        rs.getString("deliveryTip"),
                        rs.getString("available"),
                        rs.getString("coupon"),
                        rs.getString("likes")
                ),
                storeId);
    }

    public List<MenuCategory> getMenuCategories(int storeId) {
        String getMenuCategoriesQuery = "select id as menuCategoryId, name as menuCategoryName\n" +
                "                from MenuCategory where restaurantId=?";
        int getMenuCategoriesParams = storeId;
        return this.jdbcTemplate.query(getMenuCategoriesQuery,
                (rs, rowNum) -> new MenuCategory(
                        rs.getInt("menuCategoryId"),
                        rs.getString("menuCategoryName")
                ),
                storeId);
    }

    public List<Menu> getMenus(int menuCategoryId) {
        String getMenusQuery = "select F.id as menuId, F.name as menuName, F.price as menuPrice,  F.foodImg as menuImg, F.foodInfo as menuDesc, case when F.popular=1 then 'Y' else 'N' end as popularMenu,\n" +
                "               case when F.available=1 then 'Y' else 'N' end as available from Food F, MenuCategory MC where MC.id = F.category and MC.id=?" ;
        int getMenusParams = menuCategoryId;
        return this.jdbcTemplate.query(getMenusQuery,
                (rs, rowNum) -> new Menu(
                        rs.getInt("menuId"),
                        rs.getString("menuName"),
                        rs.getInt("menuPrice"),
                        rs.getString("menuImg"),
                        rs.getString("menuDesc"),
                        rs.getString("popularMenu"),
                        rs.getString("available")

                        ),
                menuCategoryId);
    }


    public List<Review> getStoreReviews(int storeId) {
        String getStoreReviewsQuery = "select R.reviewId as reviewId, U.userNickName as reviewWriter,\n" +
                "                R.rate as reviewRate, R.desc as reviewDesc,\n" +
                "                (case when timestampdiff(second , R.createdAt, current_timestamp) <60\n" +
                "        then concat(timestampdiff(second, R.createdAt, current_timestamp),' 초 전')\n" +
                "        when timestampdiff(minute , R.createdAt, current_timestamp) <60\n" +
                "        then concat(timestampdiff(minute, R.createdAt, current_timestamp),' 분 전')\n" +
                "        when timestampdiff(hour ,R.createdAt, current_timestamp) <24\n" +
                "        then concat(timestampdiff(hour, R.createdAt, current_timestamp),' 시간 전')\n" +
                "           else concat(datediff( current_timestamp, R.createdAt),' 일 전')\n" +
                "        end) as createdAt\n" +
                "        from Review R\n" +
                "        join (select userIdx, userNickName from User) as U on U.userIdx = R.userId\n" +
                "        where R.restaurantID=?"
        ;

        int getStoreReviewsParams = storeId;
        return this.jdbcTemplate.query(getStoreReviewsQuery,
                (rs, rowNum) -> new Review(
                        rs.getInt("reviewId"),
                        rs.getString("reviewWriter"),
                        rs.getString("reviewDesc"),
                        rs.getInt("reviewRate"),
                        rs.getString("createdAt")
                ),
                getStoreReviewsParams);
    }

    public List<ReviewImg> getReviewImgs(int reviewId) {
        String getReviewImgsQuery = "select reviewImgId, imgUrl as reviewImg from ReviewImg\n" +
                "        where reviewId=?"
                ;

        return this.jdbcTemplate.query(getReviewImgsQuery,
                (rs, rowNum) -> new ReviewImg(
                        rs.getInt("reviewImgId"),
                        rs.getString("reviewImg")
                ),
                reviewId);
    }

    public List<ReviewMenu> getReviewMenus(int reviewId) {
        String getReviewFoodQuery = "select (select F.id from Food F, OrderPrice where F.name=OrderPrice.menuName) as menuId, OP.menuName as menuName,\n" +
                "       case when (select F.id from Food F, OrderPrice where F.name=OrderPrice.menuName) = RR.foodId then 'Y' else 'N' end as recommend,\n" +
                "       case when (select F.id from Food F, OrderPrice where F.name=OrderPrice.menuName) = RR.foodId then RR.desc else '' end as recommendDesc\n" +
                "        from Review R join Ordered O on R.orderId = O.orderId\n" +
                "            join OrderPrice OP on O.orderId = OP.orderId\n" +
                "            join (select reviewId, foodId,`desc` from ReviewRecommend ) as RR on R.reviewId=RR.reviewId\n" +
                "                left join (select name, id from Food) as F on RR.foodId=F.id where R.reviewId=?";

//                "select OD.foodId as menuId, OD.foodName as menuName,\n" +
//                "       case when OD.foodName = F.name then 'Y' else 'N' end as recommend,\n" +
//                "       case when OD.foodName = F.name then RR.desc else '' end as recommendDesc\n" +
//                "from Review R\n" +
//                "join (select orderId from Ordered) as O on O.orderId = R.orderId\n" +
//                "left join OrderDetails OD on R.orderId = OD.orderId\n" +
//                "left join (select reviewId, foodId,`desc` from ReviewRecommend ) as RR on R.reviewId=RR.reviewId\n" +
//                "left join (select name, id from Food) as F on RR.foodId=F.id\n" +
//                "where R.reviewId=?"
//                ;

        return this.jdbcTemplate.query(getReviewFoodQuery,
                (rs, rowNum) -> new ReviewMenu(
                        rs.getInt("menuId"),
                        rs.getString("menuName"),
                        rs.getString("recommend"),
                        rs.getString("recommendDesc")
                ),
                reviewId);
    }


}