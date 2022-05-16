package com.litesuits.orm.db.model;

import com.litesuits.orm.db.utils.DataUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 属性
 *
 * @author mty
 * @date 2013 -6-9上午1:09:17
 */
public class Property implements Serializable {

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 1542861322620643038L;
    /**
     * The Column.
     */
    public String column;
    /**
     * The Field.
     */
    public Field field;
    /**
     * The Class type.
     */
    public int classType = DataUtil.CLASS_TYPE_NONE;

    //public Property() {
    //}

    /**
     * Instantiates a new Property.
     *
     * @param column the column
     * @param field  the field
     */
    public Property(String column, Field field) {
        this.column = column;
        this.field = field;
        if (this.classType <= 0) this.classType = DataUtil.getFieldClassType(field);
    }

    /**
     * Instantiates a new Property.
     *
     * @param column    the column
     * @param field     the field
     * @param classType the class type
     */
    Property(String column, Field field, int classType) {
        this.column = column;
        this.field = field;
        if (classType <= 0) this.classType = DataUtil.getFieldClassType(field);
        this.classType = classType;
    }

    /**
     * Write object.
     *
     * @param out the out
     * @throws IOException the io exception
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.column);
        out.writeObject(this.field);
        out.writeInt(this.classType);
    }

    /**
     * Read object.
     *
     * @param ins the ins
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    private void readObject(ObjectInputStream ins) throws IOException, ClassNotFoundException {
        this.column = (String) ins.readObject();
        this.field = (Field) ins.readObject();
        this.classType = ins.readInt();
    }


    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "Property{" +
                "column='" + this.column + '\'' +
                ", field=" + this.field +
                ", classType=" + this.classType +
                '}';
    }

}
