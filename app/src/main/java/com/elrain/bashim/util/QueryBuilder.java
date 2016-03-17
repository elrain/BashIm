package com.elrain.bashim.util;

import android.text.TextUtils;

import com.elrain.bashim.dal.QuotesTableHelper;

public final class QueryBuilder {

    public static String getQueryString(Constants.QueryFilter queryFilter, String filter, int count) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append(QuotesTableHelper.ID).append(", ")
                .append(QuotesTableHelper.DESCRIPTION).append(", ")
                .append(QuotesTableHelper.TITLE).append(", ")
                .append(QuotesTableHelper.PUB_DATE).append(", ")
                .append(QuotesTableHelper.LINK).append(", ")
                .append(QuotesTableHelper.IS_FAVORITE).append(", ")
                .append(QuotesTableHelper.AUTHOR).append(" FROM ")
                .append(QuotesTableHelper.TABLE).append(" WHERE ");
        if (queryFilter == Constants.QueryFilter.QUOTE || queryFilter == Constants.QueryFilter.COMICS) {
            builder.append(QuotesTableHelper.AUTHOR);
            if (queryFilter == Constants.QueryFilter.QUOTE) {
                builder.append(" IS NULL ");
                addLikeClause(filter, builder);
            } else builder.append(" IS NOT NULL ");
            builder.append(" ORDER BY ").append(QuotesTableHelper.PUB_DATE).append(" DESC, ")
                    .append(" ROWID LIMIT ").append(count);
        } else if (queryFilter == Constants.QueryFilter.FAVORITE) {
            builder.append(QuotesTableHelper.IS_FAVORITE).append("=1");
            addLikeClause(filter, builder);
        }
        return builder.toString();
    }

    private static void addLikeClause(String filter, StringBuilder builder) {
        if (!TextUtils.isEmpty(filter))
            builder.append(" AND ").append(QuotesTableHelper.DESCRIPTION)
                    .append(" LIKE '%").append(filter).append("%' ");
    }
}
