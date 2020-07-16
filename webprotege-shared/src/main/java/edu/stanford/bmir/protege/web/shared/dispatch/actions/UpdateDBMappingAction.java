package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class UpdateDBMappingAction extends AbstractNohrAction<UpdateDBMappingResult, NohrDBMapping> {

    /*private String newDBMapping;*/

    private String oldDBMapping;

    private String odbcDriver;

    private NohrColumnsTable[] selectColumnsList;

    private NohrTablesTable[] fromTablesList;

    private String predicate;

    private Integer arity;

    private String sql;

    private UpdateDBMappingAction() {
    }

    public UpdateDBMappingAction(@Nonnull ProjectId projectId,
                                 @Nonnull String odbcDriver,
                                 @Nonnull NohrColumnsTable[] selectColumnsList,
                                 @Nonnull NohrTablesTable[] fromTablesList,
                                 @Nonnull String predicate,
                                 @Nonnull Integer arity,
                                 @Nonnull String sql,
                                 /*@Nonnull String newDBMapping,*/
                                 @Nonnull String oldDBMapping) {
        super(projectId);
        this.odbcDriver = checkNotNull(odbcDriver);
        this.selectColumnsList = checkNotNull(selectColumnsList);
        this.fromTablesList = checkNotNull(fromTablesList);
        this.predicate = checkNotNull(predicate);
        this.arity = checkNotNull(arity);
        this.sql = checkNotNull(sql);
        /*this.newDBMapping = checkNotNull(newDBMapping);*/
        this.oldDBMapping = checkNotNull(oldDBMapping);


    }

    @Override
    public String toString() {
        return "UpdateDBMappingAction{" +
                /*"newDBMapping='" + newDBMapping + '\'' +*/
                ", oldDBMapping='" + oldDBMapping + '\'' +
                ", odbcDriver='" + odbcDriver + '\'' +
                ", selectColumnsList=" + Arrays.toString(selectColumnsList) +
                ", fromTablesList=" + Arrays.toString(fromTablesList) +
                ", predicate='" + predicate + '\'' +
                ", arity=" + arity +
                ", sql='" + sql + '\'' +
                '}';
    }

    public String getOdbcDriver() {
        return odbcDriver;
    }

    public NohrColumnsTable[] getSelectColumnsList() {
        return selectColumnsList;
    }

    public NohrTablesTable[] getFromTablesList() {
        return fromTablesList;
    }

    public String getPredicate() {
        return predicate;
    }

    public Integer getArity() {
        return arity;
    }

    public String getSql() {
        return sql;
    }

   /* public String getNewDBMapping() {
        return newDBMapping;
    }*/

    public String getOldDBMapping() {
        return oldDBMapping;
    }
}



