package com.managements.files.service;

import com.managements.files.dto.LoadFile;
import com.managements.files.exception.MyFileNotFoundException;
import org.springframework.stereotype.Service;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesDBService {

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    public String addFile(MultipartFile upload) {

        try {

            DBObject metadata = new BasicDBObject();
            metadata.put("fileSize", upload.getSize());

            Object fileID = template.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(), metadata);

            return fileID.toString();
        } catch (Exception e) {
            throw new MyFileNotFoundException(e.getMessage());
        }
    }


    public LoadFile downloadFile(String id) {

        try {

            GridFSFile gridFSFile = template.findOne(new Query(Criteria.where("_id").is(id)));

            LoadFile loadFile = new LoadFile();

            if (gridFSFile != null && gridFSFile.getMetadata() != null) {
                loadFile.setFilename(gridFSFile.getFilename());

                loadFile.setFileType(gridFSFile.getMetadata().get("_contentType").toString());

                loadFile.setFileSize(gridFSFile.getMetadata().get("fileSize").toString());

                loadFile.setFile(IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()));
            }

            return loadFile;
        } catch (Exception e) {
            throw new MyFileNotFoundException(e.getMessage());
        }
    }
}
