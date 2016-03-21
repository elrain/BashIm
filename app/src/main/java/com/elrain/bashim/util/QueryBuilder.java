package com.elrain.bashim.util;

import android.text.TextUtils;

import com.elrain.bashim.dal.FavoriteInfoHelper;
import com.elrain.bashim.dal.QuotesTableHelper;

public final class QueryBuilder {

    private static StringBuilder mBuilder;
    private static boolean isOrderByAdded;

    public static String getQueryString(Constants.QueryFilter queryFilter, String filter, int count) {
        createSelectBuilder();
        addSelect(QuotesTableHelper.ID, QuotesTableHelper.ALIAS);
        addSelect(QuotesTableHelper.DESCRIPTION, QuotesTableHelper.ALIAS);
        addSelect(QuotesTableHelper.TITLE, QuotesTableHelper.ALIAS);
        addSelect(QuotesTableHelper.PUB_DATE, QuotesTableHelper.ALIAS);
        addSelect(QuotesTableHelper.LINK, QuotesTableHelper.ALIAS);
        addSelect(QuotesTableHelper.IS_FAVORITE, QuotesTableHelper.ALIAS);
        addSelect(QuotesTableHelper.AUTHOR, QuotesTableHelper.ALIAS);
        if (queryFilter == Constants.QueryFilter.QUOTE || queryFilter == Constants.QueryFilter.COMICS) {
            addFrom(QuotesTableHelper.TABLE, QuotesTableHelper.ALIAS);
            if (queryFilter == Constants.QueryFilter.QUOTE)
                addWhere(QuotesTableHelper.AUTHOR + " IS NULL ", null);
            else
                addWhere(QuotesTableHelper.AUTHOR + " IS NOT NULL ", null);
            addWhere(addLikeClause(filter, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.ALIAS), " AND ");
            addOrder(QuotesTableHelper.PUB_DATE, "DESC");
            addLimit(count);
        } else if (queryFilter == Constants.QueryFilter.FAVORITE) {
            addFrom(FavoriteInfoHelper.TABLE, FavoriteInfoHelper.ALIAS);
            addJoin(QuotesTableHelper.TABLE, QuotesTableHelper.ALIAS,
                    FavoriteInfoHelper.ALIAS + "." + FavoriteInfoHelper.QUOTE_ID,
                    QuotesTableHelper.ALIAS + "." + QuotesTableHelper.ID);
            addWhere(addLikeClause(filter, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.ALIAS), " ");
            addOrder(FavoriteInfoHelper.ADDED_DATE, "DESC");
        }
        return getQuery();
    }

    private static String addLikeClause(String filter, String columnLike, String alias) {
        if (!TextUtils.isEmpty(filter))
            return alias + "." + columnLike + " LIKE '%" + filter + "%' ";
        return "";
    }

    private static void createSelectBuilder() {
        mBuilder = new StringBuilder();
        mBuilder.append("SELECT ");
        isOrderByAdded = false;
    }

    private static void addSelect(String value, String alias) {
        mBuilder.append(alias != null ? alias + "." + value : value).append(", ");
    }

    private static void addFrom(String tableName, String alias) {
        mBuilder.delete(mBuilder.length() - 2, mBuilder.length());
        mBuilder.append(" FROM ").append(tableName);
        if (!TextUtils.isEmpty(alias))
            mBuilder.append(" AS ").append(alias);
    }

    private static void addJoin(String joinTable, String joinAlias, String onLeft, String onRight) {
        mBuilder.append(" LEFT JOIN ").append(joinTable).append(" AS ").append(joinAlias).append(" ON ")
                .append(onLeft).append(" = ").append(onRight);
    }

    private static void addWhere(String whereClauses, String logic) {
        if(!TextUtils.isEmpty(whereClauses))
        if (mBuilder.indexOf(" WHERE ") != -1)
            mBuilder.append(" ").append(logic).append(" ").append(whereClauses);
        else mBuilder.append(" WHERE ").append(whereClauses);
    }

    private static void addOrder(String orderBy, String orderType) {
        mBuilder.append(" ORDER BY ").append(orderBy).append(" ").append(orderType);
        isOrderByAdded = true;
    }

    private static void addLimit(int limit) {
        if (isOrderByAdded) mBuilder.append(", ");
        mBuilder.append(" ROWID LIMIT ").append(limit);
    }

    private static String getQuery() {
        return mBuilder.toString();
    }
}
