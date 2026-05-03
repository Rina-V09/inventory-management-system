import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FeatureInventory } from './feature-inventory';

describe('FeatureInventory', () => {
  let component: FeatureInventory;
  let fixture: ComponentFixture<FeatureInventory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeatureInventory],
    }).compileComponents();

    fixture = TestBed.createComponent(FeatureInventory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
