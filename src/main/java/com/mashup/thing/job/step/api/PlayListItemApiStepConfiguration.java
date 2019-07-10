package com.mashup.thing.job.step.api;

import com.mashup.thing.config.jdbcquery.ApiStepQuery;
import com.mashup.thing.config.youtubeopenapi.YouTubeOpenApi;
import com.mashup.thing.job.step.ProviderConfiguration;
import com.mashup.thing.video.VideoMapper;
import com.mashup.thing.video.domain.Video;
import com.mashup.thing.youtube.channel.ResponseChannelYouTuber;
import com.mashup.thing.youtube.playlistitem.PlayListItem;
import com.mashup.thing.youtube.playlistitem.ResponsePlayList;
import com.mashup.thing.youtuber.domain.YouTuber;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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

@Configuration
@RequiredArgsConstructor
public class PlayListItemApiStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final WebClient webClient;
    private final YouTubeOpenApi youTubeOpenApi;
    private final ApiStepQuery apiJdbcQuery;
    private final ProviderConfiguration providerConfiguration;
    private final VideoMapper videoMapper;
    private final JdbcTemplate jdbcTemplate;

    private static final int CHUNK_SIZE = 200;

    @Bean
    public Step playListItemApiStep() throws Exception {
        return stepBuilderFactory.get("playListItemApiStep")
                .<YouTuber, ResponsePlayList>chunk(CHUNK_SIZE)
                .reader(reqPlayListItemApiReader())
                .processor(reqPlayListItemApiProcessor())
                .writer(reqPlayListItemApiWriter())
                .build();

    }

    @Bean
    public JdbcPagingItemReader<YouTuber> reqPlayListItemApiReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<YouTuber>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(YouTuber.class))
                .queryProvider(providerConfiguration.createSelectYuTuber(dataSource))
                .name("reqPlayListItemApiReader")
                .build();
    }

    @Bean
    public Function<? super YouTuber, ? extends ResponsePlayList> reqPlayListItemApiProcessor() {
        return this::updatePlayList;

    }

    private ResponsePlayList updatePlayList(YouTuber youTuber) {
        ResponseChannelYouTuber responseChannel = getYouTuberChannelInfo(youTuber.getChannelId());
        String playListId = responseChannel.getItems().get(0).getContentDetails().getRelatedPlaylists().getUploads();
        ResponsePlayList responsePlayList = getYouTuberPlayListItems(playListId);
        responsePlayList.setYouTuberId(youTuber.getId());
        return responsePlayList;

    }

    private ResponseChannelYouTuber getYouTuberChannelInfo(String channelId) {
        ResponseChannelYouTuber channelResponse = webClient.get()
                .uri(youTubeOpenApi.getChannelUrl(), youTubeOpenApi.getApiKey(),
                        "contentDetails", channelId)
                .retrieve()
                .bodyToMono(ResponseChannelYouTuber.class)
                .block();
        return channelResponse;
    }

    private ResponsePlayList getYouTuberPlayListItems(String playListId) {
        ResponsePlayList responsePlayList = webClient.get()
                .uri(youTubeOpenApi.getPlayListItemUrl(), youTubeOpenApi.getApiKey(),
                        youTubeOpenApi.getPlayListItemPart(), playListId)
                .retrieve()
                .bodyToMono(ResponsePlayList.class)
                .block();
        return responsePlayList;
    }

    @Bean
    public ItemWriter<ResponsePlayList> reqPlayListItemApiWriter() {
        return playLists -> {
            for (ResponsePlayList playList : playLists) {
                List<PlayListItem> items = playList.getItems();
                saveVideo(items, playList.getYouTuberId());
            }
        };
    }

    private void saveVideo(List<PlayListItem> items, Long youTuberId) {
        for (PlayListItem item : items) {

            Video video = videoMapper.toVideo(item.getSnippet().getTitle(),
                    item.getSnippet().getPublishedAt(),
                    item.getSnippet().getResourceId().getVideoId(),
                    item.getSnippet().getThumbnails().getMedium().getUrl(),
                    youTuberId);

            saveExecute(video);
        }
    }

    private void saveExecute(Video video) {
        jdbcTemplate.update(apiJdbcQuery.getPlayListInsertQuery(), video.getYouTuberId(), video.getPublishedAt(),
                video.getThumbnail(), video.getYoutubeVideoId(), video.getTitle());
    }

}