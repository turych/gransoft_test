package com.gwtproject.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.Random;

/**
 * QuickSort(Recursive)
 * <p>
 * Show N random numbers (depends on data entered by user in the previous screen).
 * Sort button will sort the presented numbers in descending/ascending order.
 * Clicking one of the numbers present N new random numbers on the screen.
 */
public class Main extends Composite {

    private static final MainUiBinder ourUiBinder = GWT.create(MainUiBinder.class);
    private final int MAX_RANDOM_NUMBER = 1001;
    @UiField
    InputElement numberCountInput;
    @UiField
    HTMLPanel formContainer;
    @UiField
    DockPanel numbersContainer;
    @UiField
    SimplePanel tableWrapper;
    private IntegerSortedTable table;
    /**
     * Sort order(asc/desc). Changes to opposite onClick "sort" button,
     */
    private int sortFlag = IntegerSortedTable.ORDER_DESC;

    public Main() {
        initWidget(ourUiBinder.createAndBindUi(this));
        numbersContainer.setVisible(false);
        initTable();
    }

    /**
     * Init container for numbers.
     */
    private void initTable() {
        table = new IntegerSortedTable(MAX_RANDOM_NUMBER);
        table.setCellSpacing(10);
        table.addStyleName("cell-filled");
        table.addClickHandler((event) -> {
            int colIndex = table.getCellForEvent(event).getCellIndex();
            int rowIndex = table.getCellForEvent(event).getRowIndex();
            int limit = Integer.parseInt(table.getText(rowIndex, colIndex));
            if (limit > 30) {
                new ErrorDialog("Please select a value smaller or equal to 30.").show();
                return;
            }
            table.generateRandomValues(limit);
        });
        tableWrapper.add(table);
    }


    /**
     * Generate random numbers, depends on data entered by user.
     * @param e ClickEvent
     */
    @UiHandler("submitBtn")
    protected void onSubmitBtnClick(ClickEvent e) {
        try {
            int limit = Integer.parseInt(numberCountInput.getValue());
            if (limit > 0) {
                formContainer.setVisible(false);
                numbersContainer.setVisible(true);
                table.generateRandomValues(limit);
            } else {
                new ErrorDialog("Please input only positive integer.").show();
            }
        } catch (NumberFormatException exception) {
            new ErrorDialog("Your input isn't integer.").show();
        }
    }

    /**
     * Return to home screen.
     * @param e ClickEvent
     */
    @UiHandler("resetBtn")
    protected void onResetBtnClick(ClickEvent e) {
        formContainer.setVisible(true);
        numbersContainer.setVisible(false);
    }

    /**
     * Start sort numbers.
     * @param e ClickEvent
     */
    @UiHandler("sortBtn")
    protected void onSortBtnClick(ClickEvent e) {
        table.sort(sortFlag);
        changeSortFlag();
    }

    private void changeSortFlag() {
        sortFlag = sortFlag == IntegerSortedTable.ORDER_DESC ? IntegerSortedTable.ORDER_ASC : IntegerSortedTable.ORDER_DESC;
    }

    interface MainUiBinder extends UiBinder<VerticalPanel, Main> {
    }

    /**
     * Error dialog window.
     */
    private static class ErrorDialog extends DialogBox {
        public ErrorDialog(String message) {
            center();
            setText(message);
            Button ok = new Button("OK");
            ok.addClickHandler(event -> ErrorDialog.this.hide());
            setWidget(ok);
        }
    }

    /**
     * Class generates random numbers and sorts they.
     * Algorithm: quicksort.
     */
    private static class IntegerSortedTable extends FlexTable {

        public final static int ORDER_ASC = 0;
        public final static int ORDER_DESC = 1;

        private final int maxRandom;
        private int[] numbers;

        public IntegerSortedTable(int maxRandom) {
            this.maxRandom = maxRandom;
        }

        public void sort() {
            sort(ORDER_ASC);
        }

        public void sort(int order) {
            if (numbers.length > 1) {
                int left = 0;
                int right = numbers.length - 1;
                _quick(numbers, order, left, right);
            }
            removeAllRows();
            fillTable();
        }

        public void generateRandomValues(int limit) {
            Random random = new Random();
            numbers = new int[limit];
            for (int i = 0; i < limit; i++) {
                numbers[i] = random.nextInt(maxRandom);
            }

            // set one value less or equals 30.
            int randomIndex = random.nextInt(limit);
            numbers[randomIndex] = random.nextInt(31);

            // fill table
            removeAllRows();
            fillTable();
        }

        private void _quick(int[] items, int order, int left, int right) {
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

        private void swap(int[] items, int firstIndex, int secondIndex) {
            int temp = items[firstIndex];
            items[firstIndex] = items[secondIndex];
            items[secondIndex] = temp;
        }

        private int findPivot(int[] items, int left, int right) {
            return items[(right + left) / 2];
        }

        private int partitionAsc(int[] items, int left, int right) {
            int pivot = findPivot(items, left, right);
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

        private int partitionDesc(int[] items, int left, int right) {
            int pivot = findPivot(items, left, right);
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

        private void fillTable() {
            int i = 0;
            for (int col = 0; i < numbers.length; col++) {
                for (int row = 0; row < 10 && i < numbers.length; row++, i++) {
                    setText(row, col, String.valueOf(numbers[i]));
                }
            }
        }
    }
}
