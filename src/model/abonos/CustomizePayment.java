package model.abonos;

import common.model.Abono;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomizePayment extends Abono {

    public CustomizePayment(Abono parent){
    for (Method getMethod : parent.getClass().getMethods()) {
        if (getMethod.getName().startsWith("get")) {
            try {
                Method setMethod = this.getClass().getMethod(getMethod.getName().replace("get", "set"), getMethod.getReturnType());
                setMethod.invoke(this, getMethod.invoke(parent, (Object[]) null));

            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                //not found set
            }
        }
    }
 }
    
    private String typePaymentDescription;
}
