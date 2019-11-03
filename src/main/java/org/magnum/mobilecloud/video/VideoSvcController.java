/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video;

import static org.magnum.mobilecloud.video.client.VideoSvcApi.DURATION_PARAMETER;
import static org.magnum.mobilecloud.video.client.VideoSvcApi.TITLE_PARAMETER;
import static org.magnum.mobilecloud.video.client.VideoSvcApi.VIDEO_DURATION_SEARCH_PATH;
import static org.magnum.mobilecloud.video.client.VideoSvcApi.VIDEO_SVC_PATH;
import static org.magnum.mobilecloud.video.client.VideoSvcApi.VIDEO_TITLE_SEARCH_PATH;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoSvcController {

    @Autowired
    VideoRepository videoRepository;

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}

    /**
     * This endpoint in the API returns a list of the videos that have been added to the server. The
     * Video objects should be returned as JSON. To manually test this endpoint, run your server and
     * open this URL in a browser: http://localhost:8080/video
     *
     * @return
     */
    @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.GET)
    public Collection<Video> getVideoList() {
        return (Collection<Video>) videoRepository.findAll();
    }

    /**
     * This endpoint allows clients to add Video objects by sending POST requests that have an
     * application/json body containing the Video object information.
     *
     * @param v
     * @return
     */
    @RequestMapping(value = VIDEO_SVC_PATH, method = RequestMethod.POST)
    public Video addVideo(@RequestBody Video v) {
        return videoRepository.save(v);
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}", method = RequestMethod.GET)
    public Video getVideoById(@PathVariable("id") Long id) {
        return videoRepository.findOne(id);
    }

    @RequestMapping(value = VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
    public Collection<Video> findByTitle(@RequestParam(TITLE_PARAMETER) String title) {
        return videoRepository.findByName(title);
    }

    @RequestMapping(value = VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
    public Collection<Video> findByDurationLessThan(@RequestParam(DURATION_PARAMETER) long duration) {
        return videoRepository.findByDurationLessThan(duration);
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
    public ResponseEntity<Void> likeVideo(@PathVariable("id") Long id, Principal principal) {
        Video video = videoRepository.findOne(id);
        Set<String> likedBy;
        if (video == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            likedBy = video.getLikedBy();
            if (likedBy.contains(principal.getName())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        likedBy.add(principal.getName());
        video.setLikes(video.getLikes() + 1);
        videoRepository.save(video);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = VIDEO_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
    public ResponseEntity<Void> unlikeVideo(@PathVariable("id") Long id, Principal principal) {
        Video video = videoRepository.findOne(id);
        Set<String> likedBy = video.getLikedBy();
        if (video == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (!likedBy.contains(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        likedBy.remove(principal.getName());
        if (video.getLikes() > 0) {
            video.setLikes(video.getLikes() - 1);
        }
        videoRepository.save(video);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
