package com.mashup.thing.job;

import com.mashup.thing.config.jdbcquery.VideoRenewQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import com.mashup.thing.video.VideoMapper;
import com.mashup.thing.video.domain.Video;
import com.mashup.thing.youtube.playlistitem.PlayListItem;
import com.mashup.thing.youtube.playlistitem.ResponsePlayList;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Configuration
public class VideoRenewJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final WebClient webClient;
    private final YouTubeOpenApi youTubeOpenApi;
    private final VideoRenewQuery videoRenewQuery;
    private final ProviderConfiguration providerConfiguration;
    private final VideoMapper videoMapper;
    private final JdbcTemplate jdbcTemplate;
    private final QuotaCalculator quotaCalculator;


    private static final int CHUNK_SIZE = 200;

    @Bean
    public Job videoRenewJob() throws Exception {
        return jobBuilderFactory.get("videoRenewJob")
                .start(videoRenewStep())
                .build();
    }

    @Bean
    public Step videoRenewStep() throws Exception {
        return stepBuilderFactory.get("videoRenewStep")
                .<YouTuber, ResponsePlayList>chunk(CHUNK_SIZE)
                .reader(videoRenewReader())
                .processor(videoRenewProcessor())
                .writer(videoRenewWriter())
                .build();

    }

    @Bean
    public JdbcPagingItemReader<YouTuber> videoRenewReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuber(dataSource))
                .name("youTuberRenewReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends ResponsePlayList> videoRenewProcessor() {
        return this::updatePlayList;

    }

    private ResponsePlayList updatePlayList(YouTuber youTuber) {
        String playListId = youTuber.getPlayListId();
        ResponsePlayList responsePlayList = getYouTuberPlayListItems(playListId);
        responsePlayList.setYouTuberId(youTuber.getId());
        return responsePlayList;
    }

    private ResponsePlayList getYouTuberPlayListItems(String playListId) {
        quotaCalculator.increaseQuota();
        if (quotaCalculator.isOverQuota()) {
            youTubeOpenApi.setApiKey(quotaCalculator.nextApiKey());
        }
        ResponsePlayList responsePlayList = webClient.get()
                .uri(youTubeOpenApi.getPlayListItemUrl(), youTubeOpenApi.getApiKey(),
                        youTubeOpenApi.getPlayListItemPart(), playListId)
                .retrieve()
                .bodyToMono(ResponsePlayList.class)
                .block();
        return responsePlayList;
    }


    @Bean
    public ItemWriter<ResponsePlayList> videoRenewWriter() {
        return playLists -> {
            for (ResponsePlayList playList : playLists) {
                List<PlayListItem> items = playList.getItems();
                saveVideo(items, playList.getYouTuberId());
            }
        };
    }

    private void saveVideo(List<PlayListItem> items, Long youTuberId) {
        for (PlayListItem item : items) {
            if (isVideo(item)) {
                return;
            }
            Video video = videoMapper.toVideo(item.getSnippet().getTitle(),
                    item.getSnippet().getPublishedAt(),
                    item.getSnippet().getResourceId().getVideoId(),
                    item.getSnippet().getThumbnails().getMedium().getUrl(),
                    youTuberId);

            saveExecute(video);
        }
    }

    private boolean isVideo(PlayListItem item) {
        Integer result = jdbcTemplate.queryForObject(videoRenewQuery.getVideoCountQuery(),
                Integer.class, item.getSnippet().getResourceId().getVideoId());
        if (result > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private void saveExecute(Video video) {
        jdbcTemplate.update(videoRenewQuery.getPlayListInsertQuery(), video.getYouTuberId(), video.getPublishedAt(),
                video.getThumbnail(), video.getYoutubeVideoId(), video.getTitle());
    }
}
