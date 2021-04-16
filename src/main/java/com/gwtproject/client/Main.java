package com.gwtproject.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

public class Main extends Composite {

    private final int MAX_RANDOM_NUMBER = 1001;
    private int sortFlag = ArraySort.ORDER_DESC;
    private final Label errorLabel;
    private final FlexTable table;
    private int[] numbers;

    @UiField InputElement numberCount;
    @UiField HTMLPanel errorMessageContainer;

    @UiField HTMLPanel form;
    @UiField DockPanel numbersContainer;
    @UiField SimplePanel tableContainer;

    interface MainUiBinder extends UiBinder<VerticalPanel, Main> {}

    private static MainUiBinder ourUiBinder = GWT.create(MainUiBinder.class);

    public Main() {
        initWidget(ourUiBinder.createAndBindUi(this));
        errorLabel = new Label();
        errorMessageContainer.add(errorLabel);
        numbersContainer.setVisible(false);
        table = new FlexTable();
        initTableSettings();
    }

    private void initTableSettings() {
        table.setCellSpacing(10);
        table.addStyleName("cell-filled");
        table.addClickHandler((event) -> {
            int colIndex = table.getCellForEvent(event).getCellIndex();
            int rowIndex = table.getCellForEvent(event).getRowIndex();
            int limit = Integer.parseInt(table.getText(rowIndex, colIndex));
            if (limit > 30) {
                new MyDialog().show();
                return;
            }
            loadTableContent(limit);
        });
    }


    @UiHandler("submitBtn")
    protected void onSubmitBtnClick(ClickEvent e) {
        try {
            int limit = Integer.parseInt(numberCount.getValue());
            if (limit > 0) {
                form.setVisible(false);
                numbersContainer.setVisible(true);
                loadTableContent(limit);
            } else {
                errorLabel.setText("Please input positive integer.");
            }
        } catch (NumberFormatException exception) {
            errorLabel.setText("Please input only positive integer.");
        }
    }

    @UiHandler("resetBtn")
    protected void onResetBtnClick(ClickEvent e) {
        errorLabel.setText("");
        form.setVisible(true);
        numbersContainer.setVisible(false);
    }

    @UiHandler("sortBtn")
    protected void onSortBtnClick(ClickEvent e) {
        ArraySort.quick(numbers, sortFlag);
        changeSortFlag();
        renderNumbersTable();
    }

    private void changeSortFlag() {
        sortFlag = sortFlag == ArraySort.ORDER_DESC ? ArraySort.ORDER_ASC : ArraySort.ORDER_DESC;
    }

    private void loadTableContent(int limit) {
        fillNumArrayFromRandom(limit);
        renderNumbersTable();
    }

    private void fillNumArrayFromRandom(int limit) {
        java.util.Random random = new java.util.Random();
        // Gwt подменяет Random своим классом в котором нет ints(), такое поведения для меня загадка
        // numbers = random.ints(limit, 0, MAX_RANDOM_NUMBER).toArray();
        numbers = new int[limit];
        for (int i = 0; i < limit; i++) {
            numbers[i] = random.nextInt(MAX_RANDOM_NUMBER);
        }

        int randomIndex = random.nextInt(limit);
        numbers[randomIndex] = random.nextInt(31);
    }

    private void renderNumbersTable() {
        tableContainer.remove(table);
        table.removeAllRows();
        int i = 0;
        for (int col = 0; i < numbers.length; col++) {
            for (int row = 0; row < 10 && i < numbers.length; row++, i++) {
                table.setText(row, col, String.valueOf(numbers[i]));
            }
        }
        tableContainer.add(table);
    }

    private static class MyDialog extends DialogBox {
        public MyDialog() {
            center();
            setText("Please select a value smaller or equal to 30.");
            Button ok = new Button("OK");
            ok.addClickHandler(event -> MyDialog.this.hide());
            setWidget(ok);
        }
    }

    static class ArraySort {

        public final static int ORDER_ASC = 0;
        public final static int ORDER_DESC = 1;

        public static void quick(int[] items) {
            quick(items, ORDER_ASC);
        }

        public static void quick(int[] items, int order) {
            if (items.length > 1) {
                int left = 0;
                int right = items.length - 1;
                _quick(items, order, left, right);
            }
        }

        private static void _quick(int[] items, int order, int left, int right) {
            int index;
            if (items.length > 1) {
                if (order == ORDER_DESC) {
                    index = partitionDesc(items, left, right);
                } else {
                    index = partitionAsc(items, left, right);
                }
                if (left < index - 1) {
                    _quick(items, order, left, index - 1);
                }
                if (index < right) {
                    _quick(items, order, index, right);
                }
            }

        }

        private static void swap(int[] items, int firstIndex, int secondIndex){
            int temp = items[firstIndex];
            items[firstIndex] = items[secondIndex];
            items[secondIndex] = temp;
        }

        private static int partitionAsc(int[] items, int left, int right) {
            int pivot = items[(right + left) / 2];
            while (left <= right) {
                while (items[left] < pivot) {
                    left++;
                }
                while (items[right] > pivot) {
                    right--;
                }
                if (left <= right) {
                    swap(items, left, right);
                    left++;
                    right--;
                }
            }
            return left;
        }

        private static int partitionDesc(int[] items, int left, int right) {
            int pivot = items[(right + left) / 2];
            while (left <= right) {
                while (items[left] > pivot) {
                    left++;
                }
                while (items[right] < pivot) {
                    right--;
                }
                if (left <= right) {
                    swap(items, left, right);
                    left++;
                    right--;
                }
            }
            return left;
        }
    }
}
