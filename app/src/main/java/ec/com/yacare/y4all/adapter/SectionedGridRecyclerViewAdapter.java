package ec.com.yacare.y4all.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import ec.com.yacare.y4all.activities.R;

import static ec.com.yacare.y4all.activities.R.id.nombredia;

public class SectionedGridRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId;
    private LayoutInflater mLayoutInflater;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();
    private RecyclerView mRecyclerView;


    public SectionedGridRecyclerViewAdapter(Context context, int sectionResourceId, int textResourceId,RecyclerView recyclerView,
                                            RecyclerView.Adapter baseAdapter) {

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mTextResourceId = textResourceId;
        mBaseAdapter = baseAdapter;
        mContext = context;
        mRecyclerView = recyclerView;

        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });

        final GridLayoutManager layoutManager = (GridLayoutManager)(mRecyclerView.getLayoutManager());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (isSectionHeaderPosition(position))? layoutManager.getSpanCount() : 1 ;
            }
        });
    }


    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView dia;
        public TextView mes;
        public TextView anio;
        public TextView nombreDia;

        public SectionViewHolder(View view, int mTextResourceid) {
            super(view);
            dia = (TextView) view.findViewById(R.id.dia);
            mes = (TextView) view.findViewById(R.id.mes);
            anio = (TextView) view.findViewById(R.id.anio);
            nombreDia = (TextView) view.findViewById(nombredia);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view,mTextResourceId);
        }else{
            return mBaseAdapter.onCreateViewHolder(parent, typeView -1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            String fechaCompleta = mSections.get(position).title;

//
//            SectionItem si = (SectionItem) i;
//            v = inflater.inflate(R.layout.evento_section, null);
//
//            v.setOnClickListener(null);
//            v.setOnLongClickListener(null);
//            v.setLongClickable(false);
            Typeface fontRegular = Typeface.createFromAsset(mContext.getAssets(), "Lato-Regular.ttf");

            ((SectionViewHolder)sectionViewHolder).dia.setText(fechaCompleta.substring(8,10));
            ((SectionViewHolder)sectionViewHolder).dia.setTypeface(fontRegular);

            try {
                final String mesTexto = mContext.getResources().getStringArray(R.array.meses)[Integer.valueOf(fechaCompleta.substring(5,7)) - 1];
                ((SectionViewHolder)sectionViewHolder).mes.setText(mesTexto);
                ((SectionViewHolder)sectionViewHolder).mes.setTypeface(fontRegular);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            ((SectionViewHolder)sectionViewHolder).anio.setText(fechaCompleta.substring(0,4));
            ((SectionViewHolder)sectionViewHolder).anio.setTypeface(fontRegular);
            try {
                final Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                calendar.setTime(sdf.parse(fechaCompleta));
                final String nombrediaTexto = mContext.getResources().getStringArray(R.array.dias)[calendar.get(Calendar.DAY_OF_WEEK) - 1];

                ((SectionViewHolder)sectionViewHolder).nombreDia.setText(nombrediaTexto);
                ((SectionViewHolder)sectionViewHolder).nombreDia.setTypeface(fontRegular);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            mBaseAdapter.onBindViewHolder(sectionViewHolder,sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) +1 ;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        String title;

        public Section(int firstPosition, String title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }


    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

}