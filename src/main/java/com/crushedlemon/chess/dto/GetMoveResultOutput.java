package com.crushedlemon.chess.dto;

import com.crushedlemon.chess.enums.MoveResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetMoveResultOutput {

    List<MoveResult> moveResults;

}
