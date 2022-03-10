package com.example.demo.src.review;

import com.example.demo.src.review.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReviewDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public int checkOrderInfoId(int userId, int orderInfoId){
        String checkOrderInfoIdQuery = "select(exists(select * from Ordered where userId = ? and orderId = ?))";
        Object[] checkOrderInfoIdParams = new Object[]{userId, orderInfoId};
        return this.jdbcTemplate.queryForObject(checkOrderInfoIdQuery, int.class, checkOrderInfoIdParams);
    }

    public int checkExistReview(int userId, int orderInfoId){
        String checkExistReviewQuery = "select(exists(select * from Review where userId = ? and orderId = ?))";
        Object[] checkExistReviewParams = new Object[]{userId, orderInfoId};
        return this.jdbcTemplate.queryForObject(checkExistReviewQuery, int.class, checkExistReviewParams);
    }

    public int getStoreId(int userId, int orderInfoId){
        String getStoreIdQuery = "select restaurantId from Ordered where userId = ? and orderId = ?";
        Object[] getStoreIdParams = new Object[]{userId, orderInfoId};
        return this.jdbcTemplate.queryForObject(getStoreIdQuery, int.class, getStoreIdParams);
    }

    public int createReview(int storeId, int userId, PostReviewReq postReviewReq){
        String createReviewQuery = "insert into Review(restaurantId, orderId, userId, rate, `desc`) values(?,?,?,?,?)";
        Object[] createReviewParams = new Object[]{storeId, postReviewReq.getOrderId(), userId, postReviewReq.getReviewRate(), postReviewReq.getReviewDesc()};
        this.jdbcTemplate.update(createReviewQuery, createReviewParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void createRecommend(int reviewId, int foodId, String recommendDesc){
        String createRecommendQuery = "insert into ReviewRecommend(reviewId, foodId, `desc`) values(?,?,?)";
        Object[] createRecommendParams = new Object[]{reviewId, foodId, recommendDesc};
        this.jdbcTemplate.update(createRecommendQuery, createRecommendParams);
    }

    public void createReviewImage(int reviewId, String reviewImageUrl){
        String createReviewImageQuery = "insert into ReviewImg(reviewId, imgUrl) values(?,?)";
        Object[] createReviewImageParams = new Object[]{reviewId, reviewImageUrl};
        this.jdbcTemplate.update(createReviewImageQuery, createReviewImageParams);
    }

    public int checkOrderMenuId(int orderMenuId, PostReviewReq postReviewReq){
        String checkOrderMenuIdQuery = "select case when (SELECT name from Food where id=?) in (select menuName from OrderPrice where orderId = ? )  then 1 else 0 end";
        Object[] checkOrderMenuIdParams = new Object[]{orderMenuId, postReviewReq.getOrderId()};
        return this.jdbcTemplate.queryForObject(checkOrderMenuIdQuery, int.class, checkOrderMenuIdParams);
    }

    public int checkReviewId(int userId){
        String checkReviewIdQuery = "select(exists(select * from Review where userId = ? ))";
        int checkReviewIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkReviewIdQuery, int.class, checkReviewIdParams);
    }

    public String getUserReviewCount(int userId){
        String getUserReviewCountQuery = "select concat('총 ', count(*), '개') from Review where userId = ?";
        int getUserReviewCountParams = userId;
        return this.jdbcTemplate.queryForObject(getUserReviewCountQuery, String.class, getUserReviewCountParams);
    }

    public List<Review> getStoreReviews(int storeId) {
        String getStoreReviewsQuery = "select R.reviewId  as reviewId, R2.restaurantID as storeId, R2.name as storeName, R.rate as reviewRate, R.desc as reviewDesc,\n" +
                "                                (case when timestampdiff(second , R.updatedAt, current_timestamp) <60\n" +
                "                        then concat(timestampdiff(second, R.updatedAt, current_timestamp),' 초 전')\n" +
                "                        when timestampdiff(minute , R.updatedAt, current_timestamp) <60\n" +
                "                        then concat(timestampdiff(minute, R.updatedAt, current_timestamp),' 분 전')\n" +
                "                        when timestampdiff(hour ,R.updatedAt, current_timestamp) <24\n" +
                "                        then concat(timestampdiff(hour, R.updatedAt, current_timestamp),' 시간 전')\n" +
                "                           else concat(datediff( current_timestamp, R.updatedAt),' 일 전')\n" +
                "                        end) as updatedAt from Review R join Restaurant R2 on R2.restaurantID = R.restaurantID\n" +
                "                        where userId=?"
                ;

        int getStoreReviewsParams = storeId;
        return this.jdbcTemplate.query(getStoreReviewsQuery,
                (rs, rowNum) -> new Review(
                        rs.getInt("reviewId"),
                        rs.getInt("storeId"),
                        rs.getString("storeName"),
                        rs.getInt("reviewRate"),
                        rs.getString("reviewDesc"),
                        rs.getString("updatedAt")
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

        return this.jdbcTemplate.query(getReviewFoodQuery,
                (rs, rowNum) -> new ReviewMenu(
                        rs.getInt("menuId"),
                        rs.getString("menuName"),
                        rs.getString("recommend"),
                        rs.getString("recommendDesc")
                ),
                reviewId);
    }

    public int checkReviewIdByReviewId(int userId, int reviewId){
        String checkReviewIdQuery = "select(exists(select * from Review where userId = ? and reviewId = ?))";
        Object[] checkReviewIdParams = new Object[]{userId, reviewId};
        return this.jdbcTemplate.queryForObject(checkReviewIdQuery, int.class, checkReviewIdParams);
    }

    public int checkReviewRecommendByReviewId(int reviewId){
        String checkReviewRecommendByReviewIdQuery = "select(exists(select * from ReviewRecommend where reviewId = ? ))";
        int checkReviewRecommendByReviewIdParams = reviewId;
        return this.jdbcTemplate.queryForObject(checkReviewRecommendByReviewIdQuery, int.class, checkReviewRecommendByReviewIdParams);
    }

    public int checkReviewImgByReviewId(int reviewId){
        String checkReviewImgByReviewIdQuery = "select(exists(select * from ReviewImg where reviewId = ? ))";
        int checkReviewImgByReviewIdParams = reviewId;
        return this.jdbcTemplate.queryForObject(checkReviewImgByReviewIdQuery, int.class, checkReviewImgByReviewIdParams);
    }


    public int checkStoreId(int storeId){
        String checkStoreIdQuery = "select(exists(select * from Store where id = ? ))";
        int checkStoreIdParams = storeId;
        return this.jdbcTemplate.queryForObject(checkStoreIdQuery, int.class, checkStoreIdParams);
    }

    public void deleteReviewImgByReviewId(int reviewId){
        String deleteReviewImgByReviewIdQuery = "delete from ReviewImg where reviewId=?";
        int deleteReviewImgByReviewIdParams = reviewId;
        this.jdbcTemplate.update(deleteReviewImgByReviewIdQuery, deleteReviewImgByReviewIdParams);
    }

    public void deleteReviewRecommendByReviewId( int reviewId){
        String deleteReviewRecommendByReviewIdQuery = "delete from ReviewRecommend where reviewId=?";
        int deleteReviewRecommendByReviewIdParams = reviewId;
        this.jdbcTemplate.update(deleteReviewRecommendByReviewIdQuery, deleteReviewRecommendByReviewIdParams);
    }

    public void deleteReview(int reviewId){
        String deleteCartQuery = "delete from Review where reviewId = ?";
        int deleteCartParams = reviewId;
        this.jdbcTemplate.update(deleteCartQuery, deleteCartParams);
    }


}