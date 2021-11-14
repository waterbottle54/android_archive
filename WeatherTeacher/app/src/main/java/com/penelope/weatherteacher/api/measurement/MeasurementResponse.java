package com.penelope.weatherteacher.api.measurement;

import java.util.List;

// 기상청 API 로부터 얻은 Response 객체

public class MeasurementResponse {

    private final Response response;

    public MeasurementResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "MeasurementResponse{" +
                "response=" + response +
                '}';
    }

    public static class Response {
        private final Header header;
        private final Body body;

        public Response(Header header, Body body) {
            this.header = header;
            this.body = body;
        }
        public Header getHeader() {
            return header;
        }

        public Body getBody() {
            return body;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "header=" + header +
                    ", body=" + body +
                    '}';
        }
    }

    public static class Header {
        private final String resultCode;

        public Header(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultCode() {
            return resultCode;
        }

        @Override
        public String toString() {
            return "Header{" +
                    "resultCode='" + resultCode + '\'' +
                    '}';
        }
    }

    public static class Body {
        private final Items items;

        public Body(Items items) {
            this.items = items;
        }

        public Items getItems() {
            return items;
        }

        @Override
        public String toString() {
            return "Body{" +
                    "items=" + items +
                    '}';
        }
    }

    public static class Items {
        private final List<Value> item;

        public Items(List<Value> item) {
            this.item = item;
        }

        public List<Value> getItem() {
            return item;
        }

        @Override
        public String toString() {
            return "Items{" +
                    "item=" + item +
                    '}';
        }
    }

    public static class Value {
        private final String baseDate;
        private final String baseTime;
        private final String category;
        private final String obsrValue;

        public Value(String baseDate, String baseTime, String category, String obsrValue) {
            this.baseDate = baseDate;
            this.baseTime = baseTime;
            this.category = category;
            this.obsrValue = obsrValue;
        }

        public String getBaseDate() {
            return baseDate;
        }

        public String getBaseTime() {
            return baseTime;
        }

        public String getCategory() {
            return category;
        }

        public String getObsrValue() {
            return obsrValue;
        }

        @Override
        public String toString() {
            return "Value{" +
                    "baseDate='" + baseDate + '\'' +
                    ", baseTime='" + baseTime + '\'' +
                    ", category='" + category + '\'' +
                    ", obsrValue='" + obsrValue + '\'' +
                    '}';
        }
    }

}
