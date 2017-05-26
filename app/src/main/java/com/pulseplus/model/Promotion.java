package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Bright Bridge on 10-Jan-17.
 */

public class Promotion {

    @Expose
    public ArrayList<promotion> promotion;

    @Expose
    public String Result;

    public class promotion {
        @Expose
        public String id;
        @Expose
        public String offers;
        @Expose
        public String offer_code;
        @Expose
        public String offer_type;
        @Expose
        public String minimum_order;
        @Expose
        public String description;
        @Expose
        public String valid_from;
        @Expose
        public String valid_to;
        @Expose
        public String added_date;
        @Expose
        public String updated_date;

        public String getId() {
            return id;
        }

        public String getOffers() {
            return offers;
        }

        public String getOffer_type() {
            return offer_type;
        }

        public String getMinimum_order() {
            return minimum_order;
        }

        public String getDescription() {
            return description;
        }

        public String getValid_from() {
            return valid_from;
        }

        public String getValid_to() {
            return valid_to;
        }

        public String getAdded_date() {
            return added_date;
        }

        public String getUpdated_date() {
            return updated_date;
        }

        public String getOffer_code() {
            return offer_code;
        }
    }

    public ArrayList<Promotion.promotion> getPromotion() {
        return promotion;
    }

    public String getResult() {
        return Result;
    }
}
