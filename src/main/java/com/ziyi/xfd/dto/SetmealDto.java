package com.ziyi.xfd.dto;

import com.ziyi.xfd.entity.Setmeal;
import com.ziyi.xfd.entity.SetmealDish;
import lombok.Data;
import java.util.List;

/**
 * Dto可理解为 实体扩展
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
