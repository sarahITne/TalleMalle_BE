package com.tallemalle.api.recruit;

import com.tallemalle.api.common.BaseResponse;
import com.tallemalle.api.common.Controller;
import com.tallemalle.api.recruit.model.RecruitDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class RecruitController implements Controller {
    private final RecruitService recruitService;

    public RecruitController(RecruitService recruitService) {
        this.recruitService = recruitService;
    }

    @Override
    public BaseResponse process(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (req.getRequestURI().contains("recruits") && "GET".equals(req.getMethod())) {
                String minLatStr = req.getParameter("minLat");
                String minLngStr = req.getParameter("minLng");
                String maxLatStr = req.getParameter("maxLat");
                String maxLngStr = req.getParameter("maxLng");

                if (minLatStr == null || minLngStr == null || maxLatStr == null || maxLngStr == null) {
                    return BaseResponse.fail("지도 좌표 정보(minLat, maxLat 등)가 누락되었습니다.");
                }

                double minLat = Double.parseDouble(minLatStr);
                double minLng = Double.parseDouble(minLngStr);
                double maxLat = Double.parseDouble(maxLatStr);
                double maxLng = Double.parseDouble(maxLngStr);

                List<RecruitDto.ListRes> result = recruitService.findAll(minLat, minLng, maxLat, maxLng);

                return BaseResponse.success(result);
            }
        } catch (NumberFormatException e) {
            return BaseResponse.fail("좌표 값은 숫자여야 합니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.fail("서버 에러: " + e.getMessage());
        }

        return BaseResponse.fail("지원하지 않는 요청입니다.");
    }
}
