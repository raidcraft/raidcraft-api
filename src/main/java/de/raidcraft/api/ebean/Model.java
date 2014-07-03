package de.raidcraft.api.ebean;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Expression;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Filter;
import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.JoinConfig;
import com.avaje.ebean.OrderBy;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.QueryListener;
import com.avaje.ebean.QueryResultVisitor;
import com.avaje.ebean.RawSql;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;

import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base-class for Ebean-mapped models.
 */
@MappedSuperclass
public class Model {

    /**
     * Will either insert or update this entity depending on its state.
     */
    public void save() {

        Ebean.save(this);
    }

    /**
     * Will either insert or update this entity depending on its state.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    public void save(Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).save(this);
    }

    /**
     * Updates this entity.
     */
    public void update() {

        Ebean.update(this);
    }

    /**
     * Updates this entity.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    public void update(Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).update(this);
    }

    /**
     * Deletes this entity.
     */
    public void delete() {

        Ebean.delete(this);
    }

    /**
     * Deletes this entity.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    public void delete(Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).delete(this);
    }

    /**
     * Refreshes this entity from the database.
     */
    public void refresh() {

        Ebean.refresh(this);
    }

    /**
     * Refreshes this entity from the database.
     *
     * @param clazz class, extends, registered - TODO: Add useful description
     */
    public void refresh(Class<? extends BasePlugin> clazz) {

        RaidCraft.getDatabase(clazz).refresh(this);
    }

    /**
     * Helper for Ebean queries
     *
     * @see <a href="http://www.avaje.org/static/javadoc/pub/">Ebean API documentation</a>
     */
    public static class Finder<I, T> implements Query<T> {

        private final Class<I> idType;
        private final Class<T> type;
        private final Class<? extends BasePlugin> sClazz;

        /**
         * Creates a finder for entity of type <code>T</code> with ID of type <code>I</code>, using a .. TODO: Add useful description
         *
         * @param idType ID type.
         * @param type   Entity type.
         * @param sClazz class, extends, registered - TODO: Add useful description
         */
        public Finder(Class<I> idType, Class<T> type, Class<? extends BasePlugin> sClazz) {

            this.idType = idType;
            this.type = type;
            this.sClazz = sClazz;
        }

        private EbeanServer getServer() {

            return RaidCraft.getDatabase(sClazz);
        }

        /**
         * Changes the server.
         *
         * @param clazz class, extends, registered - TODO: Add useful description
         */
        @SuppressWarnings("unchecked")
        public Finder<I, T> on(Class<? extends BasePlugin> clazz) {

            return new Finder(idType, type, clazz);
        }

        /**
         * Retrivies all entities of the given type.
         */
        public List<T> all() {

            return getServer().find(type).findList();
        }

        /**
         * Retrieves an entity by ID.
         */
        public T byId(I id) {

            return getServer().find(type, id);
        }

        /**
         * Retrieves an entity reference by ID.
         */
        public T ref(I id) {

            return getServer().getReference(type, id);
        }

        /**
         * Returns the next identity value.
         */
        @SuppressWarnings("unchecked")
        public I nextId() {

            return (I) getServer().nextId(type);
        }

        /**
         * Creates a filter for sorting and filtering lists of entities locally without going back to the database.
         */
        public Filter<T> filter() {

            return getServer().filter(type);
        }

        /**
         * Creates a query.
         */
        public Query<T> query() {

            return getServer().find(type);
        }

        /**
         * Cancels query execution, if supported by the underlying database and driver.
         */
        public void cancel() {

            query().cancel();
        }

        /**
         * Copies this query.
         */
        public Query<T> copy() {

            return query().copy();
        }

        /**
         * Specifies a path to load including all its properties.
         */
        public Query<T> fetch(String path) {

            return query().fetch(path);
        }

        /**
         * Specifies a path to fetch with a specific list properties to include, to load a partial object.
         */
        public Query<T> fetch(String path, String fetchProperties) {

            return query().fetch(path, fetchProperties);
        }

        /**
         * Additionally specifies a <code>FetchConfig</code> to specify a 'query join' and/or define the lazy loading query.
         */
        public Query<T> fetch(String path, FetchConfig fetchConfig) {

            return query().fetch(path, fetchConfig);
        }

        /**
         * Additionally specifies a <code>FetchConfig</code> to use a separate query or lazy loading to load this path.
         */
        public Query<T> fetch(String assocProperty, String fetchProperties, FetchConfig fetchConfig) {

            return query().fetch(assocProperty, fetchProperties, fetchConfig);
        }

        /**
         * USE: <code>Query<T> fetch(String path)</code>
         *
         * @deprecated
         */
        public Query<T> join(String path) {

            throw new UnsupportedOperationException();
        }

        /**
         * USE: <code>Query<T> fetch(String path, String fetchProperties</code>
         *
         * @deprecated
         */
        public Query<T> join(String path, String joinProperties) {

            throw new UnsupportedOperationException();
        }

        /**
         * USE: <code>Query<T> fetch(String path, FetchConfig fetchConfig)</code>
         *
         * @deprecated
         */
        public Query<T> join(String path, JoinConfig joinConfig) {

            throw new UnsupportedOperationException();
        }

        /**
         * USE: <code>Query<T> fetch(String assocProperty, String fetchProperties, FetchConfig fetchConfig)</code>
         *
         * @deprecated
         */
        public Query<T> join(String assocProperty, String joinProperties, JoinConfig joinConfig) {

            throw new UnsupportedOperationException();
        }

        /**
         * Executes a find IDs query in a background thread.
         */
        public FutureIds<T> findFutureIds() {

            return query().findFutureIds();
        }

        /**
         * Executes a find list query in a background thread.
         */
        public FutureList<T> findFutureList() {

            return query().findFutureList();
        }

        /**
         * Executes a find row count query in a background thread.
         */
        public FutureRowCount<T> findFutureRowCount() {

            return query().findFutureRowCount();
        }

        /**
         * Executes a query and return the resuls as a list of IDs.
         */
        public List<Object> findIds() {

            return query().findIds();
        }

        /**
         * Executes the query and returns the results as a list of objects.
         */
        public List<T> findList() {

            return query().findList();
        }

        /**
         * Executes the query and returns the results as a map of objects.
         */
        public Map<?, T> findMap() {

            return query().findMap();
        }

        /**
         * Executes the query and returns the results as a map of the objects.
         */
        public <K> Map<K, T> findMap(String a, Class<K> b) {

            return query().findMap(a, b);
        }

        /**
         * Returns a <code>PagingList</code> for this query.
         */
        public PagingList<T> findPagingList(int pageSize) {

            return query().findPagingList(pageSize);
        }

        /**
         * Returns the number of entities this query should return.
         */
        public int findRowCount() {

            return query().findRowCount();
        }

        /**
         * Executes the query and returns the results as a set of objects.
         */
        public Set<T> findSet() {

            return query().findSet();
        }

        /**
         * Executes the query and returns the results as either a single bean or <code>null</code>, if no matching bean is found.
         */
        public T findUnique() {

            return query().findUnique();
        }

        /**
         * Returns a <code>QueryResultVisitor</code> for this query.
         */
        public void findVisit(QueryResultVisitor<T> visitor) {

            query().findVisit(visitor);
        }

        /**
         * Returns the <code>QueryIterator</code> for this query.
         */
        public QueryIterator<T> findIterate() {

            return query().findIterate();
        }

        /**
         * Returns the <code>ExpressionFactory</code> used by this query.
         */
        public ExpressionFactory getExpressionFactory() {

            return query().getExpressionFactory();
        }

        /**
         * Returns the first row value.
         */
        public int getFirstRow() {

            return query().getFirstRow();
        }

        /**
         * Returns the SQL that was generated for executing this query.
         */
        public String getGeneratedSql() {

            return query().getGeneratedSql();
        }

        /**
         * Returns the maximum of rows for this query.
         */
        public int getMaxRows() {

            return query().getMaxRows();
        }

        /**
         * Sets the index value to query.
         */
        public Query<T> setUseIndex(UseIndex useIndex) {

            return query().setUseIndex(useIndex);
        }

        /**
         * Gets the index value for this query.
         */
        public UseIndex getUseIndex() {

            return query().getUseIndex();
        }

        /**
         * Returns the type of query.
         */
        public Type getType() {

            return query().getType();
        }

        /**
         * Returns the <code>RawSql</code> that was set to use for this query.
         */
        public RawSql getRawSql() {

            return query().getRawSql();
        }

        /**
         * Returns the query's <code>having</code> clause.
         */
        public ExpressionList<T> having() {

            return query().having();
        }

        /**
         * Adds an expression to the <code>having</code> clause and returns the query.
         */
        public Query<T> having(Expression addExpressionToHaving) {

            return query().having(addExpressionToHaving);
        }

        /**
         * Adds clauses to the <code>having</code> clause and returns the query.
         */
        public Query<T> having(String addToHavingClause) {

            return query().having(addToHavingClause);
        }

        /**
         * Returns <code>true</code> if this query was tuned by <code>autoFetch</code>.
         */
        public boolean isAutofetchTuned() {

            return query().isAutofetchTuned();
        }

        /**
         * Returns the <code>order by</code> clause so that you can append an ascending or descending property to the <code>order by</code> clause.
         * <p/>
         * This is exactly the same as {@link #orderBy}.
         */
        public OrderBy<T> order() {

            return query().order();
        }

        /**
         * Sets the <code>order by</code> clause, replacing the existing <code>order by</code> clause if there is one.
         * <p/>
         * This is exactly the same as {@link #orderBy(String)}.
         */
        public Query<T> order(String orderByClause) {

            return query().order(orderByClause);
        }

        /**
         * Returns the <code>order by</code> clause so that you can append an ascending or descending property to the <code>order by</code> clause.
         * <p/>
         * This is exactly the same as {@link #order}.
         */
        public OrderBy<T> orderBy() {

            return query().orderBy();
        }

        /**
         * Set the <code>order by</code> clause replacing the existing <code>order by</code> clause if there is one.
         * <p/>
         * This is exactly the same as {@link #order(String)}.
         */
        public Query<T> orderBy(String orderByClause) {

            return query().orderBy(orderByClause);
        }

        /**
         * Explicitly sets a comma delimited list of the properties to fetch on the 'main' entity bean, to load a partial object.
         */
        public Query<T> select(String fetchProperties) {

            return query().select(fetchProperties);
        }

        /**
         * Explicitly specifies whether to use 'Autofetch' for this query.
         */
        public Query<T> setAutofetch(boolean autofetch) {

            return query().setAutofetch(autofetch);
        }

        /**
         * Sets the rows after which fetching should continue in a background thread.
         */
        public Query<T> setBackgroundFetchAfter(int backgroundFetchAfter) {

            return query().setBackgroundFetchAfter(backgroundFetchAfter);
        }

        /**
         * Sets a hint, which for JDBC translates to <code>Statement.fetchSize()</code>.
         */
        public Query<T> setBufferFetchSizeHint(int fetchSize) {

            return query().setBufferFetchSizeHint(fetchSize);
        }

        /**
         * Sets whether this query uses <code>DISTINCT</code>.
         */
        public Query<T> setDistinct(boolean isDistinct) {

            return query().setDistinct(isDistinct);
        }

        /**
         * Set this to true and the beans and collections returned will be plain classes rather than Ebean generated dynamic subclasses etc.
         */
        public Query<T> setVanillaMode(boolean isVanillaMode) {

            return query().setVanillaMode(isVanillaMode);
        }

        /**
         * Sets the first row to return for this query.
         */
        public Query<T> setFirstRow(int firstRow) {

            return query().setFirstRow(firstRow);
        }

        /**
         * Sets the ID value to query.
         */
        public Query<T> setId(Object id) {

            return query().setId(id);
        }

        /**
         * Sets a listener to process the query on a row-by-row basis.
         */
        public Query<T> setListener(QueryListener<T> queryListener) {

            return query().setListener(queryListener);
        }

        /**
         * When set to <code>true</code>, all the beans from this query are loaded into the bean cache.
         */
        public Query<T> setLoadBeanCache(boolean loadBeanCache) {

            return query().setLoadBeanCache(loadBeanCache);
        }

        /**
         * Sets the property to use as keys for a map.
         */
        public Query<T> setMapKey(String mapKey) {

            return query().setMapKey(mapKey);
        }

        /**
         * Sets the maximum number of rows to return in the query.
         */
        public Query<T> setMaxRows(int maxRows) {

            return query().setMaxRows(maxRows);
        }

        /**
         * Replaces any existing <code>order by</code> clause using an <code>OrderBy</code> object.
         * <p/>
         * This is exactly the same as {@link #setOrderBy(com.avaje.ebean.OrderBy)}.
         */
        public Query<T> setOrder(OrderBy<T> orderBy) {

            return query().setOrder(orderBy);
        }

        /**
         * Set an OrderBy object to replace any existing <code>order by</code> clause.
         * <p/>
         * This is exactly the same as {@link #setOrder(com.avaje.ebean.OrderBy)}.
         */
        public Query<T> setOrderBy(OrderBy<T> orderBy) {

            return query().setOrderBy(orderBy);
        }

        /**
         * Sets an ordered bind parameter according to its position.
         */
        public Query<T> setParameter(int position, Object value) {

            return query().setParameter(position, value);
        }

        /**
         * Sets a named bind parameter.
         */
        public Query<T> setParameter(String name, Object value) {

            return query().setParameter(name, value);
        }

        /**
         * Sets the OQL query to run
         */
        public Query<T> setQuery(String oql) {

            return getServer().createQuery(type, oql);
        }

        /**
         * Sets <code>RawSql</code> to use for this query.
         */
        public Query<T> setRawSql(RawSql rawSql) {

            return query().setRawSql(rawSql);
        }

        /**
         * Sets whether the returned beans will be read-only.
         */
        public Query<T> setReadOnly(boolean readOnly) {

            return query().setReadOnly(readOnly);
        }

        /**
         * Sets a timeout on this query.
         */
        public Query<T> setTimeout(int secs) {

            return query().setTimeout(secs);
        }

        /**
         * Sets whether to use the bean cache.
         */
        public Query<T> setUseCache(boolean useBeanCache) {

            return query().setUseCache(useBeanCache);
        }

        /**
         * Sets whether to use the query cache.
         */
        public Query<T> setUseQueryCache(boolean useQueryCache) {

            return query().setUseQueryCache(useQueryCache);
        }

        /**
         * Adds expressions to the <code>where</code> clause with the ability to chain on the <code>ExpressionList</code>.
         */
        public ExpressionList<T> where() {

            return query().where();
        }

        /**
         * Applies a filter on the 'many' property list rather than the root level objects.
         */
        public ExpressionList<T> filterMany(String propertyName) {

            return query().filterMany(propertyName);
        }

        /**
         * Adds a single <code>Expression</code> to the <code>where</code> clause and returns the query.
         */
        public Query<T> where(Expression expression) {

            return query().where(expression);
        }

        /**
         * Adds additional clauses to the <code>where</code> clause.
         */
        public Query<T> where(String addToWhereClause) {

            return query().where(addToWhereClause);
        }
    }
}