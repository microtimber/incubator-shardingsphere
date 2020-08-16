/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.route.type.hint;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.core.route.type.RoutingEngine;
import org.apache.shardingsphere.core.route.type.RoutingResult;
import org.apache.shardingsphere.core.route.type.RoutingUnit;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.strategy.route.hint.HintShardingStrategy;
import org.apache.shardingsphere.core.strategy.route.value.ListRouteValue;
import org.apache.shardingsphere.core.strategy.route.value.RouteValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class HintDatabaseShardingOnlyRoutingEngine implements RoutingEngine {
    private Logger logger = LoggerFactory.getLogger(HintDatabaseShardingOnlyRoutingEngine.class);
    
    private final ShardingRule shardingRule;

    @Override
    public RoutingResult route() {
        RoutingResult result = new RoutingResult();
        if (HintManager.isDatabaseShardingOnly() && !isRoutingByHint()) {
            logger.error("Settle DatabaseShardingOnly in HintManager, but can not find default database sharding strategy.");
            return result;
        }
        for (String each : shardingRule.getDefaultDatabaseShardingStrategy()
                .doSharding(shardingRule.getShardingDataSourceNames().getDataSourceNames(),
                        getRouteValues(HintManager.getDatabaseShardingValues()))) {
            result.getRoutingUnits().add(new RoutingUnit(each));
        }
        return result;
    }

    private boolean isRoutingByHint() {
        return shardingRule.getDefaultDatabaseShardingStrategy() instanceof HintShardingStrategy;
    }

    private List<RouteValue> getRouteValues(final Collection<Comparable<?>> shardingValue) {
        return shardingValue.isEmpty() ? Collections.<RouteValue>emptyList() : Collections.<RouteValue>singletonList(new ListRouteValue<>("", "", shardingValue));
    }
}
