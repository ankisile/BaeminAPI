package com.example.demo.src.cart;

import com.example.demo.src.cart.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CartDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetMenuInfoRes getMenuInfoRes(int menuId) {
        String getMenuInfoResQuery = "select F.id as menuId, F.name as menuName,\n" +
                "                F.foodInfo as menuDesc,\n" +
                "        F.price as menuPrice, F.foodImg as menuImg\n" +
                "        from Food F \n" +
                "         where F.id = ?";
        int getMenuInfoResParams = menuId;
        return this.jdbcTemplate.queryForObject(getMenuInfoResQuery,
                (rs, rowNum) -> new GetMenuInfoRes(
                        rs.getInt("menuId"),
                        rs.getString("menuName"),
                        rs.getString("menuDesc"),
                        rs.getInt("menuPrice"),
                        rs.getString("menuImg")),
                menuId);
    }

    public List<MenuOptionCategory> getMenuOptionCategories(int menuId) {
        String getMenuOptionCategoriesQuery = "select id as menuOptionCategoryId, name as menuOptionCategoryName\n" +
                "                from FoodCategory where foodId=?";
        int getMenuOptionCategoriesParams = menuId;
        return this.jdbcTemplate.query(getMenuOptionCategoriesQuery,
                (rs, rowNum) -> new MenuOptionCategory(
                        rs.getInt("menuOptionCategoryId"),
                        rs.getString("menuOptionCategoryName")
                ),
                getMenuOptionCategoriesParams);
    }

    public List<MenuOption> getMenuOptions(int menuCategoryId) {
        String getMenusQuery = "select FO.id as optionId, FO.name as optionName,\n" +
                "        FO.price as optionPrice\n" +
                "        from FoodOption FO, FoodCategory FC where FC.id = FO.detailedCategory and FC.id=?";
        int getMenusParams = menuCategoryId;
        return this.jdbcTemplate.query(getMenusQuery,
                (rs, rowNum) -> new MenuOption(
                        rs.getInt("optionId"),
                        rs.getString("optionName"),
                        rs.getInt("optionPrice")
                ),
                menuCategoryId);
    }



    public GetMenuPriceRes getMenuPriceRes(int menuId, int count) {
        String getMenuPriceResQuery = "select id as menuId, concat(format(price * ?, 0), '원') as menuPrice " +
                "from Food where id = ?";
        Object[] getMenuPriceResParams = new Object[]{count, menuId};
        return this.jdbcTemplate.queryForObject(getMenuPriceResQuery,
                (rs, rowNum) -> new GetMenuPriceRes(
                        rs.getInt("menuId"),
                        rs.getString("menuPrice")),
                getMenuPriceResParams
        );
    }

    public int checkMenuId(int menuId) {
        String checkMenuIdQuery = "select(exists(select * from Food where  id = ?))";
        int checkMenuIdParams = menuId;
        return this.jdbcTemplate.queryForObject(checkMenuIdQuery, int.class, checkMenuIdParams);
    }

    public int checkCartId(int userId) {
        String checkCartIdQuery = "select(exists(select * from Cart where userId = ?))";
        int checkCartIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkCartIdQuery, int.class, checkCartIdParams);
    }

    public int checkSameStoreMenu(int userId, int menuId) {
        String checkSameStoreMenuQuery = "select case when (select restaurantID from Food where id = ?) =\n" +
                "                 (select restaurantID from Food where id = (select foodId from CartMenu CM inner join Cart C on C.id=CM.cartId where C.userId = ?  limit 1) )\n" +
                "                then 1 else 0 end";
        Object[] checkSameStoreMenuParams = new Object[]{menuId, userId};
        return this.jdbcTemplate.queryForObject(checkSameStoreMenuQuery, int.class, checkSameStoreMenuParams);
    }

    public int makeCart(int userId) {
        String makeCartQuery = "insert into Cart (userId) values (?)";
        int makeCartParams = userId;
        this.jdbcTemplate.update(makeCartQuery, makeCartParams);

        String lastInsertIdQuery = "select last_insert_id()"; //마지막으로 넣은 pk값 반환
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void makeCartMenu(int userId, int cartId, PostCartReq postCartReq) {
        String makeCartMenuQuery = "insert into CartMenu (userId, cartId, foodId, count) values (?,?,?,?)";
        Object[] makeCartMenuParams = new Object[]{ userId, cartId, postCartReq.getMenuId(), postCartReq.getCount()};
        this.jdbcTemplate.update(makeCartMenuQuery, makeCartMenuParams);
    }

    public int checkCartMenuIdByCartMenuId(int userId, int cartMenuId){
        String checkCartMenuIdQuery = "select(exists(select * from CartMenu where userId = ? and id = ?))";
        Object[] checkCartMenuIdParams = new Object[]{userId, cartMenuId};
        return this.jdbcTemplate.queryForObject(checkCartMenuIdQuery, int.class, checkCartMenuIdParams);
    }

    public int checkCartMenuId(int userId, int menuId) {
        String checkCartMenuIdQuery = "select(exists(select * from CartMenu where foodId = ? and userId = ?))";
        Object[] checkCartMenuIdParams = new Object[]{menuId, userId};
        return this.jdbcTemplate.queryForObject(checkCartMenuIdQuery, int.class, checkCartMenuIdParams);
    }



    public void modifyCartMenuCount(int userId, PostCartReq postCartReq) {
        String modifyCartMenuCountQuery = "update CartMenu set count = count + ? where foodId = ? and userId = ?";
        Object[] modifyCartMenuCountParams = new Object[]{postCartReq.getCount(), postCartReq.getMenuId(), userId};
        this.jdbcTemplate.update(modifyCartMenuCountQuery, modifyCartMenuCountParams);
    }



    public int getCartId(int userId){
        String getCartIdQuery = "select id from Cart where userId = ? order by id DESC limit 1";
        int getCartIdParams = userId;
        return this.jdbcTemplate.queryForObject(getCartIdQuery, int.class, getCartIdParams);
    }

    public int checkCartMenuIdByUserId(int userId){
        String checkCartMenuIdQuery = "select(exists(select * from CartMenu where userId = ? ))";
        int checkCartMenuIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkCartMenuIdQuery, int.class, checkCartMenuIdParams);
    }


    public void deleteCartMenuByCartMenuId(int userId, int cartMenuId){
        String deleteCartMenuByCartMenuIdQuery = "delete from CartMenu where id = ? and userId = ?";
        Object[] deleteCartMenuByCartMenuIdParams = new Object[]{cartMenuId, userId};
        this.jdbcTemplate.update(deleteCartMenuByCartMenuIdQuery, deleteCartMenuByCartMenuIdParams);
    }

    public void deleteCartMenu(int userId){
        String deleteCartMenuQuery = "delete CM from CartMenu CM inner join  Cart C on C.id=CM.cartId where C.userId=?;";
        int deleteCartMenuParams = userId;
        this.jdbcTemplate.update(deleteCartMenuQuery, deleteCartMenuParams);
    }

    public void deleteCart(int userId){
        String deleteCartQuery = "delete from Cart where userId = ?";
        int deleteCartParams = userId;
        this.jdbcTemplate.update(deleteCartQuery, deleteCartParams);
    }

    public List<GetCartMenuRes> getCartMenuRes(int userId){
        String cartMenusListQuery = "select cm.id as cartMenuId, F.foodImg as menuImg, F.name as cartMenuName, count as cartMenuCount, concat(format(count * price, 0), '원') as totalPrice\n" +
                "                from CartMenu cm inner join Food F on cm.foodId=F.id inner join Cart C on cm.cartId = C.id\n" +
                "                where C.userId = ?";
        int cartMenusListParams = userId;
        return this.jdbcTemplate.query(cartMenusListQuery,
                (rs, rowNum) -> new GetCartMenuRes(
                        rs.getInt("cartMenuId"),
                        rs.getString("menuImg"),
                        rs.getString("cartMenuName"),
                        rs.getInt("cartMenuCount"),
                        rs.getString("totalPrice")),
                cartMenusListParams
        );
    }

    public int getOrderPrice(int userId){
        String getOrderPriceQuery = "select sum(cm.count * F.price) as orderPrice\n" +
                "from CartMenu cm, Food F\n" +
                "where cm.foodId = F.id and userId = ?";
        int getOrderPriceParams = userId;
        return this.jdbcTemplate.queryForObject(getOrderPriceQuery, int.class, getOrderPriceParams);
    }

    public int getDeliveryFee(int userId){
        String getDeliveryFeeQuery = "select MIN(case when D.maxTip is null then D.minTip\n" +
                "    else (case when (select sum(CM.count*F.price) from CartMenu CM \n" +
                "inner join Food F on CM.foodId = F.id where CM.userId=?)>D.criteriaPrice\n" +
                "            then D.minTip else D.maxTip end)\n" +
                "    end) as deliveryFee from Delivery D \n" +
                "where D.restaurantID = (select restaurantID from CartMenu CM2 JOIN Food F2 on F2.id = CM2.foodId WHERE CM2.userId=?)";
        Object[] getDeliveryFeeParams = new Object[] {userId, userId};
        return this.jdbcTemplate.queryForObject(getDeliveryFeeQuery, int.class, getDeliveryFeeParams);
    }

    public String getUserAddress(int userId){
        String getUserAddressQuery = "select concat(address, ' ', details) as address from UserAddress where userId = ? and represent=1";
        int getUserAddressParams = userId;
        return this.jdbcTemplate.queryForObject(getUserAddressQuery, String.class, getUserAddressParams);
    }


    public int makeOrderInfo(int userId, int storeId, String address, int totalPrice, PostOrderReq postOrderReq){
        String makeOrderInfoQuery = "insert into Ordered(userId,restaurantId,deliveryAddr, totalPrice, toOwner, toRider, payment) values(?,?,?,?,?,?,?)";
        Object[] makeOrderInfoParams = new Object[]{userId,storeId,address,totalPrice, postOrderReq.getToOwnerMessage(), postOrderReq.getToRiderMessage(),postOrderReq.getPayment()};
        this.jdbcTemplate.update(makeOrderInfoQuery, makeOrderInfoParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public List<CartMenus> cartMenusList(int userId){
        String cartMenusListQuery = "select F.name as menuName, count, F.price as price from CartMenu join Food F on F.id = CartMenu.foodId\n" +
                "where userId = ?";
        int cartMenusListParams = userId;
        return this.jdbcTemplate.query(cartMenusListQuery,
                (rs, rowNum) -> new CartMenus(
                        rs.getString("menuName"),
                        rs.getInt("price"),
                        rs.getInt("count")),
                cartMenusListParams
        );
    }

    public void makeOrderInfoPrice(int orderId, String menuName, int count, int price){
        String makeOrderInfoPriceQuery = "insert into OrderPrice(orderId, menuName, count, menuPrice) values(?,?,?,?)";
        Object[] makeOrderInfoPriceParams = new Object[]{orderId, menuName, count, price};
        this.jdbcTemplate.update(makeOrderInfoPriceQuery, makeOrderInfoPriceParams);
    }


    public int getStore(int userId){
        String getStoreQuery = "select restaurantID from Food where id = (select foodId from CartMenu where userId = ? limit 1)";
        int getStoreParams = userId;
        return this.jdbcTemplate.queryForObject(getStoreQuery, int.class, getStoreParams);
    }
}