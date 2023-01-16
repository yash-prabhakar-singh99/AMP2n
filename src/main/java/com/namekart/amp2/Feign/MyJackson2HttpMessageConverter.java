package com.namekart.amp2.Feign;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2021/12/9
 */
public class MyJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public MyJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_XML);
        mediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        mediaTypes.add(MediaType.APPLICATION_PROBLEM_XML);
        mediaTypes.add(MediaType.APPLICATION_RSS_XML);
        mediaTypes.add(MediaType.APPLICATION_XHTML_XML);

        setSupportedMediaTypes(mediaTypes);
    }
}