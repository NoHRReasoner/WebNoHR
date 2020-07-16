package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class GetDBMappingInfoResult extends AbstractNohrResult<NohrDBMapping> {

    private String odbcDriver;

    private List<NohrColumnsTable>  selectColumnsList;

    private List<NohrTablesTable> fromTablesList;

    private String predicate;

    private Integer arity;

    private String sql;

    private NohrResponseCodes code;

    private GetDBMappingInfoResult() {
    }

    public GetDBMappingInfoResult(@Nonnull ProjectId projectId,
                                  @Nonnull EventList<ProjectEvent<?>> eventList,
                                  @Nonnull String odbcDriver,
                                  @Nonnull List<NohrColumnsTable> selectColumnsList,
                                  @Nonnull List<NohrTablesTable> fromTablesList,
                                  @Nonnull String predicate,
                                  @Nonnull Integer arity,
                                  @Nonnull String sql,
                                  @Nonnull NohrResponseCodes code) {
        super(projectId, eventList);
        this.odbcDriver = odbcDriver;
        this.selectColumnsList = selectColumnsList;
        this.fromTablesList = fromTablesList;
        this.predicate = predicate;
        this.arity = arity;
        this.sql = sql;
        this.code = code;

    }

    public String getOdbcDriver() {
        return odbcDriver;
    }

    public List<NohrColumnsTable> getSelectColumnsList() {
        return selectColumnsList;
    }

    public List<NohrTablesTable> getFromTablesList() {
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

    public NohrResponseCodes getCode() {
        return code;
    }
}
