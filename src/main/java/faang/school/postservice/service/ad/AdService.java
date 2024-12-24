package faang.school.postservice.service.ad;

import faang.school.postservice.dto.ad.AdDto;

public interface AdService {

    void removeExpiredAds(int maxListSize);

    AdDto buyAd(AdDto dto);
}