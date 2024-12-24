package faang.school.postservice.service.ad;


import faang.school.postservice.model.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdServiceAsyncImpl implements AdServiceAsync {
    private final AdRepository adRepository;

    @Async("fixedThreadPool")
    @Override
    public void deleteExpiredAdsByBatch(List<Ad> ads) {
        adRepository.deleteAllInBatch(ads);
    }
}