package Dto.Response;

public record ProductPublishResponse(
        boolean success,
        String productId,   // ID sản phẩm vừa tạo (null nếu thất bại)
        String auctionId,   // ID phiên đấu giá vừa tạo (null nếu thất bại)
        String message
) {
}
