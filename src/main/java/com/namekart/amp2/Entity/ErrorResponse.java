package com.namekart.amp2.Entity;

public class ErrorResponse {
    Responsee Response;

    public ErrorResponse() {
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "Response=" + Response +
                '}';
    }

    public Responsee getResponse() {
        return Response;
    }

    public void setResponse(Responsee response) {
        Response = response;
    }
}
