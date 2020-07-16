package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class CreateDBMappingAction extends AbstractNohrAction<CreateDBMappingResult, NohrDBMapping> {

    private String odbcDriver;

    private NohrColumnsTable[] selectColumnsList;

    private NohrTablesTable[] fromTablesList;

    private String predicate;

    private Integer arity;

    private String sql;

    private CreateDBMappingAction() {
    }

    public CreateDBMappingAction(@Nonnull ProjectId projectId,
                                 @Nonnull String odbcDriver,
                                 @Nonnull NohrColumnsTable[] selectColumnsList,
                                 @Nonnull NohrTablesTable[] fromTablesList,
                                 @Nonnull String predicate,
                                 @Nonnull Integer arity,
                                 @Nonnull String sql) {
        super(projectId);
        this.odbcDriver = checkNotNull(odbcDriver);
        this.selectColumnsList = selectColumnsList;
        this.fromTablesList = fromTablesList;
        this.predicate = checkNotNull(predicate);
        this.arity = checkNotNull(arity);
        this.sql = checkNotNull(sql);

    }

    @Override
    public String toString() {
        return toStringHelper("CreateDBMappingAction")
                .add("sourceText", getODBC())
                .toString();
    }

    public String getODBC() {
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

}



