package tc.tlouro_c;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import tc.tlouro_c.item.Item;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class RootWrapper {

    private List<Item> items;

}
