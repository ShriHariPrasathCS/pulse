package com.pulseplus.web;

import com.pulseplus.model.CancelOrder;
import com.pulseplus.model.Cart;
import com.pulseplus.model.CartMedicineDelete;
import com.pulseplus.model.ChatBean;
import com.pulseplus.model.ContactUs;
import com.pulseplus.model.ContinuePendingOrder;
import com.pulseplus.model.EditCart;
import com.pulseplus.model.EditProfile;
import com.pulseplus.model.EveningDelivery;
import com.pulseplus.model.MorningDelivery;
import com.pulseplus.model.OTPVerification;
import com.pulseplus.model.OfferUpdate;
import com.pulseplus.model.OrderHistory;
import com.pulseplus.model.OrderHistoryChat;
import com.pulseplus.model.OrderIdGen;
import com.pulseplus.model.ProfileModule;
import com.pulseplus.model.Promotion;
import com.pulseplus.model.Register;
import com.pulseplus.model.Reorder;
import com.pulseplus.model.ResendOTP;
import com.pulseplus.model.SaveToCart;
import com.pulseplus.model.SendMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    //User_Registration
    @POST("/pulseplus/api/index.php/UserRegister")
    Call<Register.Result> registerUser(@Body Register register);

    //OTP_verification
    @POST("/pulseplus/api/index.php/otp_verification")
    Call<OTPVerification.Result> otpVerify(@Body OTPVerification verification);

    //resend OTP
    @GET("/pulseplus/api/index.php/resend_otp_user/{userId}")
    Call<ResendOTP> resendOtp(@Path("userId") String userId);

    //Generate_orderId
    @POST("/pulseplus/api/index.php/order_generate")
    Call<OrderIdGen.Result> orderIdGen(@Body OrderIdGen orderIdGen);

    //http://52.34.89.223/pulseplus/api/index.php/getchatby_order/ODRID001(orderid)
    @GET("/pulseplus/api/index.php/getchatby_order/{orderId}")
    Call<ChatBean> chatBean(@Path("orderId") String orderId);

    //http://52.34.89.223/pulseplus/api/index.php/addchat
    @POST("/pulseplus/api/index.php/addchat")
    Call<SendMessage.Result> sendMessage(@Body SendMessage sendMessage);

    @GET("/pulseplus/api/index.php/user_details/{userId}")
    Call<ProfileModule> profile(@Path("userId") String userId);

    @POST("/pulseplus/api/index.php/editprofile")
    Call<EditProfile.Result> editProfile(@Body EditProfile editProfile);

    @POST("/pulseplus/api/index.php/contactus")
    Call<ContactUs.Result> contactUs(@Body ContactUs contactus);

    @GET("/pulseplus/api/index.php/order_history/{userid}")
    Call<OrderHistory> orderHistory(@Path("userid") String userid);

//    @GET("/pulseplus/api/index.php/get_pending_order/{userid}")
//    Call<PendingOrder> pendingOrders(@Path("userid") String userid);

    @GET("/pulseplus/api/index.php/promotion/{user_id}")
    Call<Promotion> promotion(@Path("user_id") String id);

    @GET("/pulseplus/api/index.php/chat_history/{orderid}")
    Call<OrderHistoryChat> orderHistoryChat(@Path("orderid") String orderid);

    //http://52.34.89.223/pulseplus/api/index.php/reorder_generate
    @POST("/pulseplus/api/index.php/reorder_generate")
    Call<Reorder.Response> reorder(@Body Reorder.Request request);

    //Continue Pending Order
    @GET("/pulseplus/api/index.php/continue_order/{Orderid}")
    Call<ContinuePendingOrder> continuePendingOrder(@Path("Orderid") String Orderid);

    @GET("/pulseplus/api/index.php/order_cancel/{orderid}")
    Call<CancelOrder> cancelOrder(@Path("orderid") String orderid);

    @GET("/pulseplus/api/index.php/save_cart/{orderid}")
    Call<SaveToCart> saveToCart(@Path("orderid") String orderid);

    @GET("/pulseplus/api/index.php/offer_update/{user_id}/{offer_code}")
    Call<OfferUpdate> offerUpdate(@Path("user_id") String user_id, @Path("offer_code") String offer_code);

    @GET("/pulseplus/api/index.php/cart_list/{orderid}")
    Call<Cart> cart(@Path("orderid") String orderid);

    @POST("/pulseplus/api/index.php/delete_cart")
    Call<CartMedicineDelete.Response> cartMedicineDelete(@Body CartMedicineDelete.Request request);

    //schedule delivery morning
    @GET("/pulseplus/api/index.php/morning_delivery/{orderid}")
    Call<MorningDelivery> morningDelivery(@Path("orderid") String orderid);

    // Schedule Delivery Evening
    @GET("/pulseplus/api/index.php/evening_delivery/{orderid}")
    Call<EveningDelivery> eveningDelivery(@Path("orderid") String orderid);

    //Send Cart Value
    @POST("/pulseplus/api/index.php/edit_cart")
    Call<EditCart.Response> editCart(@Body EditCart.Request request);
}
