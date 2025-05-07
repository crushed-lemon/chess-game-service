package com.crushedlemon.chess.engine;

import com.crushedlemon.chess.commons.model.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuleEngineFactory {

    @Autowired
    private ClassicRuleEngine classicRuleEngine;

    @Autowired
    private DefaultRuleEngine defaultRuleEngine;

    public RuleEngine getRuleEngine(GameType gameType) {
        if (gameType.equals(GameType.GAME_TYPE_CLASSIC)) {
            return classicRuleEngine;
        }
        return defaultRuleEngine;
    }

}
