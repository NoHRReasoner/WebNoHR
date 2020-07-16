package edu.stanford.bmir.protege.web.server.nohrdata;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import edu.stanford.bmir.protege.web.server.inject.MongoDatabaseProvider;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettingsImpl;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettingsImpl;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMappingImpl;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRuleImpl;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrRepository {

    private final String NOHR_RULES_COLLECTION = "NoHRRules";
    private final String NOHR_RULES_BUCKET = "RulesBucket";
    private final String NOHR_DBMAPPINGS_COLLECTION = "NoHRDBMappings";
    private final String NOHR_SETTINGS_COLLECTION = "NoHRSettings";
    private final String NOHR_DATABASE_SETTINGS_COLLECTION = "NoHRDatabaseSettings";
    private final String PROJECT_ID = "ProjectID";
    private final String RULES_CHANGED = "RulesChanged";
    private final String RULES = "Rules";
    private final String DBMAPPINGS = "dbMappings";
    private final String DBSETTINGS = "dbSettings";

    private final String EL = "DLInferenceEngineEL";
    private final String QL = "DLInferenceEngineQL";
    private final String RL = "DLInferenceEngineRL";
    private final String DLENGINE = "DLInferenceEngine";

    private final String HERMIT = "HERMIT";
    private final String KONCLUDE = "KONCLUDE";

    private static final Logger logger = LoggerFactory.getLogger(NohrRepository.class);
    MongoCollection<Document> rulesCollection;
    MongoCollection<Document> dbMappingsCollection;
    MongoCollection<Document> settingsCollection;
    MongoCollection<Document> databaseSettingsCollection;
    GridFSBucket gridFSBucket;

    private static NohrRepository instance;

    public static NohrRepository getInstance(MongoDatabaseProvider databaseProvider) {
        if (instance == null) {
            instance = new NohrRepository(databaseProvider);
        }
        return instance;
    }

    private NohrRepository(MongoDatabaseProvider databaseProvider) {
        try {
            /*logger.info("Getting Nohr collection from mongoDB");*/
            rulesCollection = databaseProvider.get().getCollection(NOHR_RULES_COLLECTION);
            dbMappingsCollection = databaseProvider.get().getCollection(NOHR_DBMAPPINGS_COLLECTION);
            settingsCollection = databaseProvider.get().getCollection(NOHR_SETTINGS_COLLECTION);
            databaseSettingsCollection = databaseProvider.get().getCollection(NOHR_DATABASE_SETTINGS_COLLECTION);
            gridFSBucket = GridFSBuckets.create(databaseProvider.get(), NOHR_RULES_BUCKET);
        } catch (Exception e) {
            logger.info(e.getMessage());
            System.out.println(e);
            /*throw new Exception("Connection to database failed",e);*/
        }
    }

    public List<NohrDatabaseSettings> getDatabaseSettings(ProjectId projectID) {
        Document found = databaseSettingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Getting nohr connection to database settings to {} from database", projectID);

        List<List<String>> dbSettingsList = new LinkedList<List<String>>();
        List<NohrDatabaseSettings> nohrDBSettingsList = new LinkedList<NohrDatabaseSettings>();
        NohrDatabaseSettings nohrDBSettings = new NohrDatabaseSettingsImpl();
        if (found != null) {
            dbSettingsList = (List<List<String>>) found.get(DBSETTINGS);

            for (List<String> settings : dbSettingsList) {

                nohrDBSettings = new NohrDatabaseSettingsImpl(settings.get(0), settings.get(1), settings.get(2), settings.get(3), settings.get(4));
                nohrDBSettingsList.add(nohrDBSettings);

            }

            if (nohrDBSettingsList.isEmpty())
                deleteDocument(databaseSettingsCollection, projectID);

        }
        return nohrDBSettingsList;
    }

    public void setDatabaseSettings(ProjectId projectID, List<NohrDatabaseSettings> nohrDBSettings) {
        Document found = databaseSettingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Setting nohr connection to database settings to {} in database", projectID);

        List<List<String>> dbSettingsList;
        List<String> nohrDBStringList = new LinkedList<String>();

        if (found != null) {
            dbSettingsList = new LinkedList<List<String>>();

            nohrDBSettings = nohrDBSettings.stream()
                    .distinct()
                    .collect(Collectors.toList());

            for (NohrDatabaseSettings elem : nohrDBSettings) {
                nohrDBStringList = new LinkedList<String>();
                nohrDBStringList.add(elem.getConnectionName());
                nohrDBStringList.add(elem.getDatabaseName());
                nohrDBStringList.add(elem.getDatabaseType());
                nohrDBStringList.add(elem.getUsername());
                nohrDBStringList.add(elem.getPassword());
                dbSettingsList.add(nohrDBStringList);
            }

            Document updateSettings = new Document(DBSETTINGS, dbSettingsList);
            Document updateOperation = new Document("$set", updateSettings);

            databaseSettingsCollection.updateOne(found, updateOperation);
        } else {
            dbSettingsList = new LinkedList<List<String>>();

            for (NohrDatabaseSettings elem : nohrDBSettings) {
                nohrDBStringList = new LinkedList<String>();
                nohrDBStringList.add(elem.getConnectionName());
                nohrDBStringList.add(elem.getDatabaseName());
                nohrDBStringList.add(elem.getDatabaseType());
                nohrDBStringList.add(elem.getUsername());
                nohrDBStringList.add(elem.getPassword());
                dbSettingsList.add(nohrDBStringList);
            }

            Document nohrSettings = new Document();
            nohrSettings.put(PROJECT_ID, projectID.getId());
            nohrSettings.put(DBSETTINGS, dbSettingsList);

            databaseSettingsCollection.insertOne(nohrSettings);
        }
    }

    /*public void setChangedRules(ProjectId projectID, boolean rulesChanged) {
        Document found = rulesCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Setting changedRules flag to value {} to {} in database", rulesChanged, projectID);
        if (found != null) {
            Bson updateValue = new Document(RULES_CHANGED, rulesChanged);
            Bson updateOperation = new Document("$set", updateValue);
            rulesCollection.updateOne(found, updateOperation);
        } else {
            logger.info("{} rules not found", projectID);
        }
    }*/

    /*public Boolean getChangedRules(ProjectId projectID) {
        Document found = rulesCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Getting changedRules flag to {} from database", projectID);

        if (found != null)
            return found.getBoolean(RULES_CHANGED);

        logger.info("{} rules not found", projectID);
        return null;
    }*/

    public void insertRule(ProjectId projectID, NohrRule rule) throws IOException {
        Bson bsonFilter = Filters.eq("metadata.ProjectID", projectID.getId());
        GridFSFile rulesGridFile = gridFSBucket.find(bsonFilter).first();

        logger.info("Inserting a new rule to {} in database", projectID);

        if (rulesGridFile != null) {

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(projectID.getId(), byteArray);
            byteArray.write((rule.getRule() + "\n").getBytes());

            gridFSBucket.delete(rulesGridFile.getObjectId());

            InputStream streamToUploadFrom = new ByteArrayInputStream(byteArray.toByteArray());

            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(358400)
                    .metadata(new Document(PROJECT_ID, projectID.getId()));

            ObjectId fileId = gridFSBucket.uploadFromStream(projectID.getId(), streamToUploadFrom, options);

        } else {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byteArray.write((rule.getRule() + "\n").getBytes());

            InputStream streamToUploadFrom = new ByteArrayInputStream(byteArray.toByteArray());

            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(358400)
                    .metadata(new Document(PROJECT_ID, projectID.getId()));

            ObjectId fileId = gridFSBucket.uploadFromStream(projectID.getId(), streamToUploadFrom, options);
        }
    }

    public void insertRules(ProjectId projectID, Collection<NohrRule> rules) throws IOException {
        Bson bsonFilter = Filters.eq("metadata.ProjectID", projectID.getId());
        GridFSFile rulesGridFile = gridFSBucket.find(bsonFilter).first();
        if (rulesGridFile != null)
            gridFSBucket.delete(rulesGridFile.getObjectId());

        logger.info("Inserting rules array to {} in database", projectID);

        /*LinkedList<String> rulesList = new LinkedList<>();
        List<String> insertRules = rules.stream().map(NohrRule::getRule).collect(Collectors.toList());
        rulesList.addAll(insertRules);

        String ruleJoiner = String.join("\n", rulesList);*/

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        for (NohrRule rule : rules)
            byteArray.write((rule.getRule() + "\n").getBytes());

        InputStream streamToUploadFrom = new ByteArrayInputStream(byteArray.toByteArray());

        GridFSUploadOptions options = new GridFSUploadOptions()
                .chunkSizeBytes(358400)
                .metadata(new Document(PROJECT_ID, projectID.getId()));

        ObjectId fileId = gridFSBucket.uploadFromStream(projectID.getId(), streamToUploadFrom, options);
    }

    public void updateRule(ProjectId projectID, String oldRule, String newRule) {
        Bson bsonFilter = Filters.eq("metadata.ProjectID", projectID.getId());
        GridFSFile rulesGridFile = gridFSBucket.find(bsonFilter).first();
        logger.info("Updating rule {} to {} to {} in database", oldRule, newRule, projectID);
        if (rulesGridFile != null) {

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(projectID.getId(), byteArray);

            String fileString = new String(byteArray.toByteArray(), StandardCharsets.UTF_8);
            String[] rulesArray = fileString.split("\n");
            LinkedList<String> rulesList = new LinkedList<>();
            rulesList.addAll(Arrays.asList(rulesArray));

            int index;
            if ((index = rulesList.indexOf(oldRule)) != -1)
                rulesList.set(index, newRule);


            String ruleJoiner = String.join("\n", rulesList).concat("\n");

            byte[] byteArrayToUpload = convertToBytes(ruleJoiner);

            gridFSBucket.delete(rulesGridFile.getObjectId());

            InputStream streamToUploadFrom = new ByteArrayInputStream(byteArrayToUpload);

            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(358400)
                    .metadata(new Document(PROJECT_ID, projectID.getId()));

            ObjectId fileId = gridFSBucket.uploadFromStream(projectID.getId(), streamToUploadFrom, options);
            rulesList.clear();


        } else {
            logger.info("{} not found", projectID);
            return;
        }
    }

    public void deleteRules(ProjectId projectID, Collection<NohrRule> rules) {
        Bson bsonFilter = Filters.eq("metadata.ProjectID", projectID.getId());
        GridFSFile rulesGridFile = gridFSBucket.find(bsonFilter).first();

        logger.info("Deleting rule(s) to {} in database", projectID);
        if (rulesGridFile != null) {

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(projectID.getId(), byteArray);

            String fileString = new String(byteArray.toByteArray(), StandardCharsets.UTF_8);
            String[] file = fileString.split("\n");
            LinkedList<String> rulesList = new LinkedList<>();
            rulesList.addAll(Arrays.asList(file));

            List<String> deleteRules = rules.stream().map(NohrRule::getRule).collect(Collectors.toList());

            rulesList.removeAll(deleteRules);

            gridFSBucket.delete(rulesGridFile.getObjectId());

            if(!rulesList.isEmpty()) {
                String ruleJoiner = String.join("\n", rulesList).concat("\n");

                byte[] byteArrayToUpload = convertToBytes(ruleJoiner);


                InputStream streamToUploadFrom = new ByteArrayInputStream(byteArrayToUpload);

                GridFSUploadOptions options = new GridFSUploadOptions()
                        .chunkSizeBytes(358400)
                        .metadata(new Document(PROJECT_ID, projectID.getId()));

                ObjectId fileId = gridFSBucket.uploadFromStream(projectID.getId(), streamToUploadFrom, options);
            }
            rulesList.clear();

        } else {
            logger.info("{} not found", projectID);
            return;
        }
    }

    public List<NohrRule> getRules(ProjectId projectID) {
        Bson bsonFilter = Filters.eq("metadata.ProjectID", projectID.getId());
        GridFSFile rulesGridFile = gridFSBucket.find(bsonFilter).first();

        List<NohrRule> nohrRules = new LinkedList<>();
        LinkedList<String> rulesList = new LinkedList<>();

        logger.info("Getting rules to {} from database", projectID);
        if (rulesGridFile != null) {
            if (rulesGridFile.getLength() == 0) {
                gridFSBucket.delete(rulesGridFile.getObjectId());
                return new LinkedList<>();
            }

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(projectID.getId(), byteArray);

            String fileString = new String(byteArray.toByteArray(), StandardCharsets.UTF_8);
            String[] file = fileString.split("\n");

            rulesList.addAll(Arrays.asList(file));

        } else {
            logger.info("{} rules not found", projectID);
            return new LinkedList<>();
        }

        nohrRules = rulesList.stream().map(NohrRuleImpl::new).collect(Collectors.toList());
        rulesList.clear();
        return nohrRules;
    }

    private void deleteDocument(MongoCollection<Document> collection, ProjectId projectID) {
        logger.info("deleting database document (" + collection.getNamespace() + ") of {}", projectID);
        collection.deleteOne(new Document(PROJECT_ID, projectID.getId()));
    }

    public void setSettings(ProjectId projectID, NohrSettings settings) {
        Document found = settingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Setting nohr settings to {} in database", projectID);

        if (found != null) {
            Document updateSettings = new Document(EL, settings.getELSetting());
            updateSettings.append(QL, settings.getQLSetting());
            updateSettings.append(RL, settings.getRLSetting());
            updateSettings.append(DLENGINE, settings.getDLEngineSetting());
            Document updateOperation = new Document("$set", updateSettings);
            settingsCollection.updateOne(found, updateOperation);

        } else {
            Document nohrSettings = new Document();
            nohrSettings.put(PROJECT_ID, projectID.getId());
            nohrSettings.put(EL, settings.getELSetting());
            nohrSettings.put(QL, settings.getQLSetting());
            nohrSettings.put(RL, settings.getRLSetting());
            nohrSettings.put(DLENGINE, settings.getDLEngineSetting());

            settingsCollection.insertOne(nohrSettings);
        }
    }

    public NohrSettings getSettings(ProjectId projectID) {
        Document found = settingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Getting nohr settings to {} from database", projectID);

        boolean DLInferenceEngineEL, DLInferenceEngineQL, DLInferenceEngineRL;
        String DLInferenceEngine;

        NohrSettings nohrSettings;


        if (found != null) {
            DLInferenceEngineEL = found.getBoolean(EL);
            DLInferenceEngineQL = found.getBoolean(QL);
            DLInferenceEngineRL = found.getBoolean(RL);
            DLInferenceEngine = found.getString(DLENGINE);

            nohrSettings = new NohrSettingsImpl(DLInferenceEngineEL, DLInferenceEngineQL, DLInferenceEngineRL, DLInferenceEngine);

        } else {
            Document nohrDBSettings = new Document();
            nohrDBSettings.put(PROJECT_ID, projectID.getId());
            nohrDBSettings.put(EL, true);
            nohrDBSettings.put(QL, true);
            nohrDBSettings.put(RL, true);
            nohrDBSettings.put(DLENGINE, HERMIT);

            nohrSettings = new NohrSettingsImpl(true, true, true, HERMIT);

            settingsCollection.insertOne(nohrDBSettings);
        }
        return nohrSettings;
    }

    public void insertDBMapping(ProjectId projectID, NohrDBMapping dbMapping) {
        Document found = dbMappingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Inserting a new dbMapping to {} in database", projectID);

        if (found != null) {

            Bson updateValue = new Document(DBMAPPINGS, dbMapping.getDbMapping());
            Document updateOperation = new Document("$addToSet", updateValue);
            dbMappingsCollection.updateOne(found, updateOperation);
        } else {
            Document nohrDBMappings = new Document();
            nohrDBMappings.put(PROJECT_ID, projectID.getId());
            nohrDBMappings.put(DBMAPPINGS, Collections.singletonList(dbMapping.getDbMapping()));

            dbMappingsCollection.insertOne(nohrDBMappings);
        }
    }

    public void insertDBMappings(ProjectId projectID, Collection<NohrDBMapping> dbMappings) {
        Document found = dbMappingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Inserting dbMappings array to {} in database", projectID);

        List<String> db_dbMappings = new LinkedList<>();

        /*for (NohrDBMapping r : dbMappings)
            db_dbMappings.add(r.getDbMapping());*/

        db_dbMappings = dbMappings.stream().map(NohrDBMapping::getDbMapping).collect(Collectors.toList());

        if (found != null) {
            Document updateValues = new Document(DBMAPPINGS, db_dbMappings);
            Document updateOperation = new Document("$set", updateValues);

            dbMappingsCollection.updateOne(found, updateOperation);
        } else {
            Document nohrDBMappings = new Document();
            nohrDBMappings.put(PROJECT_ID, projectID.getId());
            nohrDBMappings.put(DBMAPPINGS, db_dbMappings);

            dbMappingsCollection.insertOne(nohrDBMappings);
        }
    }

    public void updateDBMapping(ProjectId projectID, String oldDBMapping, String newDBMapping) {
        Document found = dbMappingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        logger.info("Updating dbMapping {} to {} to {} in database", oldDBMapping, newDBMapping, projectID);
        if (found != null) {
            List<String> dbMappings = (List<String>) found.get(DBMAPPINGS);
            int index = dbMappings.indexOf(oldDBMapping);
            if (index != -1 && !dbMappings.contains(newDBMapping)) {
                Bson newValue = new Document(DBMAPPINGS + "." + index, newDBMapping);
                Document updateOperation = new Document("$set", newValue);

                dbMappingsCollection.updateOne(found, updateOperation);
            } else {
                logger.info("dbMapping not found in {}", projectID);
            }

        } else {
            logger.info("{} not found", projectID);
            return;
        }
    }

    public void deleteDBMapping(ProjectId projectID, Collection<NohrDBMapping> dbMappings) {
        Document found = dbMappingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();
        List<String> db_dbMappings = new LinkedList<>();
        logger.info("Deleting dbMapping(s) to {} in database", projectID);
        if (found != null) {
            if (found.get(DBMAPPINGS) == null) {
                logger.info("{} dbMappings not found", projectID);
                return;
            }
            for (NohrDBMapping r : dbMappings)
                db_dbMappings.add(r.getDbMapping());

            Bson removeValue = new Document(DBMAPPINGS, db_dbMappings);
            Document removeUpdateOperation = new Document("$pullAll", removeValue);
            dbMappingsCollection.updateOne(found, removeUpdateOperation);
        } else {
            logger.info("{} dbMappings not found", projectID);
            return;
        }
    }

    public List<NohrDBMapping> getDBMappings(ProjectId projectID) {
        Document found = dbMappingsCollection.find(new Document(PROJECT_ID, projectID.getId())).first();

        List<String> db_dbMappings;
        List<NohrDBMapping> nohrDBMappings = new LinkedList<>();

        logger.info("Getting dbMappings to {} from database", projectID);
        if (found != null) {
            db_dbMappings = (List<String>) found.get(DBMAPPINGS);
            if (db_dbMappings == null) {
                logger.info("{} dbMappings not found", projectID);
                return new LinkedList<>();
            } else if (db_dbMappings.isEmpty()) {
                deleteDocument(dbMappingsCollection, projectID);
                return new LinkedList<>();
            }
        } else {
            logger.info("{} dbMappings not found", projectID);
            return new LinkedList<>();
        }

        nohrDBMappings = db_dbMappings.stream().map(NohrDBMappingImpl::new).collect(Collectors.toList());

        /*for (String dbMapping : db_dbMappings)
            nohrDBMappings.add(new NohrDBMappingImpl(dbMapping));*/

        return nohrDBMappings;
    }

    private byte[] convertToBytes(String str) {
        /*String str = Arrays.toString(strings);*/
        Charset charset = StandardCharsets.UTF_8;
        byte[] data = str.getBytes(charset);

        return data;
    }

}
